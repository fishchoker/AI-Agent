package cn.bugstack.ai.infrastructure.ingest;

import cn.bugstack.ai.domain.ingest.model.IngestResult;
import cn.bugstack.ai.domain.ingest.service.GitIngestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Service
public class GitIngestServiceImpl implements GitIngestService {

    @Resource
    private TokenTextSplitter tokenTextSplitter;

    @Resource
    private PgVectorStore pgVectorStore;

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public IngestResult ingest(String repoUrl,
                               String branch,
                               List<String> parseOptions,
                               String knowledgeName,
                               String ragId,
                               String baseDir) {
        try {
            if (repoUrl == null || repoUrl.isEmpty()) {
                throw new IllegalArgumentException("缺少参数 repoUrl");
            }
            String repoName = extractRepoName(repoUrl);
            Path target = Paths.get(baseDir, "git-ingest", repoName + "-" + System.currentTimeMillis());
            Files.createDirectories(target);

            List<String> cmd = new ArrayList<>();
            cmd.add("git");
            cmd.add("clone");
            cmd.add("--depth"); cmd.add("1");
            if (branch != null && !branch.isEmpty()) { cmd.add("-b"); cmd.add(branch); }
            cmd.add(repoUrl);
            cmd.add(target.toString());
            int exit = runProcess(cmd, null, target.getParent());
            if (exit != 0) throw new RuntimeException("git clone 失败，exit=" + exit);

            List<Path> files = listFiles(target);
            files = filterByParseOptions(files, parseOptions);

            int parsedFiles = 0, documents = 0, chunks = 0;
            List<Document> allChunks = new ArrayList<>();

            for (Path p : files) {
                try {
                    byte[] bytes = Files.readAllBytes(p);
                    org.springframework.core.io.Resource res = new ByteArrayResource(bytes) {
                        @Override public String getFilename() { return p.getFileName().toString(); }
                    };
                    TikaDocumentReader reader = new TikaDocumentReader(res);
                    List<Document> docs = reader.get();
                    documents += (docs != null ? docs.size() : 0);
                    List<Document> piece = tokenTextSplitter.apply(docs);
                    String knowledge = firstNonEmpty(knowledgeName, repoName, "default-knowledge");
                    for (Document d : piece) {
                        d.getMetadata().put("knowledge", knowledge);
                        if (ragId != null && !ragId.isEmpty()) d.getMetadata().put("ragId", ragId);
                        d.getMetadata().put("source_path", p.toString());
                    }
                    chunks += piece.size();
                    allChunks.addAll(piece);
                    parsedFiles++;
                } catch (Exception ex) {
                    log.warn("解析失败，跳过：{} -> {}", p, ex.getMessage());
                }
            }

            if (!allChunks.isEmpty()) {
                PgVectorStore vectorStoreToUse = resolveDynamicPgVectorStore();
                log.info("GitIngest 使用PgVectorStore: {}", vectorStoreToUse.getClass().getName());
                vectorStoreToUse.accept(allChunks);
            }

            return IngestResult.builder()
                    .repoUrl(repoUrl)
                    .branch(branch)
                    .clonedPath(target.toString())
                    .fileCount(files.size())
                    .parsedFiles(parsedFiles)
                    .documents(documents)
                    .chunks(chunks)
                    .knowledge(firstNonEmpty(knowledgeName, repoName))
                    .ragId(ragId)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private PgVectorStore resolveDynamicPgVectorStore() {
        try {
            String[] beanNames = applicationContext.getBeanNamesForType(PgVectorStore.class);
            log.info("GitIngest 可用PgVectorStore Beans: {}", java.util.Arrays.toString(beanNames));
            for (String beanName : beanNames) {
                if (beanName.startsWith("vectorStore_")) {
                    log.info("GitIngest 选择动态PgVectorStore: {}", beanName);
                    return applicationContext.getBean(beanName, PgVectorStore.class);
                }
            }
        } catch (Exception e) {
            log.warn("GitIngest 获取动态PgVectorStore失败，使用默认: {}", e.getMessage());
        }
        return pgVectorStore;
    }

    private static String firstNonEmpty(String... arr) {
        for (String s : arr) if (s != null && !s.isEmpty()) return s; return null;
    }

    private static String extractRepoName(String repoUrl) {
        String s = repoUrl; s = s.replaceAll("/+$", "");
        int slash = s.lastIndexOf('/');
        String name = slash >= 0 ? s.substring(slash + 1) : s;
        if (name.endsWith(".git")) name = name.substring(0, name.length() - 4);
        return name;
    }

    private static int runProcess(List<String> cmd, Map<String, String> env, Path workDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (env != null) pb.environment().putAll(env);
        if (workDir != null) pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line; while ((line = br.readLine()) != null) { log.info("[git] {}", line); }
        }
        return p.waitFor();
    }

    private static List<Path> listFiles(Path root) throws IOException {
        List<Path> all = new ArrayList<>();
        Files.walk(root).filter(Files::isRegularFile).forEach(all::add);
        return all;
    }

    private static List<Path> filterByParseOptions(List<Path> files, List<String> opts) {
        if (opts == null || opts.isEmpty()) return files;
        Set<String> set = new HashSet<>();
        for (Object o : opts) set.add(String.valueOf(o).toLowerCase());
        return files.stream().filter(p -> {
            String name = p.getFileName().toString().toLowerCase();
            String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
            if (set.contains("readme") && name.startsWith("readme")) return true;
            if (set.contains("docs")) {
                if (ext.equals("md") || ext.equals("pdf") || ext.equals("docx") || ext.equals("txt")) return true;
            }
            if (set.contains("code")) {
                if (ext.matches("(java|kt|ts|tsx|js|py|go|rs|c|cpp|cs|rb|php|scala|sql|yml|yaml|json|xml)")) return true;
            }
            return set.isEmpty();
        }).toList();
    }
}
