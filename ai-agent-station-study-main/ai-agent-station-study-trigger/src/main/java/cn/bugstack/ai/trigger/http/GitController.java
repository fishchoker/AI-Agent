package cn.bugstack.ai.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import jakarta.annotation.Resource;
import cn.bugstack.ai.domain.ingest.service.GitIngestService;
import cn.bugstack.ai.domain.ingest.model.IngestResult;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/git/")
public class GitController {

    @Resource
    private GitIngestService gitIngestService;

    @RequestMapping(value = "test-connection", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> testGitConnection(@RequestBody Map<String, Object> request) {
        try {
            String repoUrl = (String) request.get("repoUrl");
            String repoType = (String) request.get("repoType");
            String authType = (String) request.get("authType");
            String accessToken = (String) request.get("accessToken");

            log.info("测试Git连接，请求：repoUrl={} repoType={} authType={}", repoUrl, repoType, authType);

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) throw new IllegalArgumentException("无效的 GitHub 仓库 URL");
            String owner = parts[0];
            String repo = parts[1].replaceAll(".git$", "");

            String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> apiResponse = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

            Map<String, Object> response = new HashMap<>();
            if (apiResponse.getStatusCode() == HttpStatus.OK) {
                response.put("code", "0000");
                response.put("info", "GitHub 连接测试成功");
                Map<String, Object> data = new HashMap<>();
                data.put("connected", true);
                data.put("repoUrl", repoUrl);
                data.put("repoName", repo);
                data.put("owner", owner);
                data.put("repoType", repoType);
                data.put("authType", authType);
                response.put("data", data);
                return ResponseEntity.ok(response);
            } else {
                response.put("code", "5000");
                response.put("info", "GitHub 连接失败，状态码: " + apiResponse.getStatusCodeValue());
                response.put("data", null);
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", "5000");
            response.put("info", "GitHub 连接测试异常：" + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 解析Git仓库 -> 固定执行：克隆+解析+向量入库
     */
    @RequestMapping(value = "parse-repo", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> parseGitRepository(@RequestBody Map<String, Object> request) {
        try {
            String repoUrl = (String) request.get("repoUrl");
            String repoType = (String) request.get("repoType");
            String authType = (String) request.get("authType");
            String accessToken = (String) request.get("accessToken");
            String branch = (String) request.getOrDefault("branch", "");
            String ragId = (String) request.get("ragId");
            String knowledgeBaseId = (String) request.get("knowledgeBaseId");
            String knowledgeBaseName = (String) request.get("knowledgeBaseName");
            String knowledgeName = (String) request.get("knowledgeName");
            Object parseOptions = request.get("parseOptions");
            String defaultBaseDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "docs", "dev-ops", "rag-file").toString();
            String baseDir = asStringOrDefault(request.get("baseDir"), defaultBaseDir);

            log.info("解析并入库，请求：repoUrl={} repoType={} authType={} branch={} ragId={}", repoUrl, repoType, authType, branch, ragId);

            String[] parts = repoUrl.replace("https://github.com/", "").split("/");
            if (parts.length < 2) throw new IllegalArgumentException("无效的 GitHub 仓库 URL");
            String owner = parts[0];
            String repo = parts[1].replaceAll(".git$", "");

            String apiRepo = String.format("https://api.github.com/repos/%s/%s", owner, repo);
            String apiContents = String.format("https://api.github.com/repos/%s/%s/contents", owner, repo);
            if (branch != null && !branch.isEmpty()) apiContents = apiContents + "?ref=" + branch;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            if (accessToken != null && !accessToken.isEmpty()) headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> repoResp = restTemplate.exchange(apiRepo, HttpMethod.GET, entity, Map.class);
            if (repoResp.getStatusCode() != HttpStatus.OK) {
                return error("获取仓库信息失败，状态码: " + repoResp.getStatusCodeValue());
            }
            Map<String, Object> repoInfo = repoResp.getBody();
            ResponseEntity<List> contentsResp = restTemplate.exchange(apiContents, HttpMethod.GET, entity, List.class);

            Map<String, Object> data = new HashMap<>();
            data.put("repoId", repoInfo.get("id"));
            data.put("repoName", repoInfo.get("name"));
            data.put("owner", repoInfo.get("owner"));
            data.put("description", repoInfo.get("description"));
            data.put("language", repoInfo.get("language"));
            data.put("stars", repoInfo.get("stargazers_count"));
            data.put("forks", repoInfo.get("forks_count"));
            data.put("size", repoInfo.get("size"));
            data.put("defaultBranch", repoInfo.get("default_branch"));
            data.put("createdAt", repoInfo.get("created_at"));
            data.put("updatedAt", repoInfo.get("updated_at"));
            data.put("repoUrl", repoUrl);
            data.put("repoType", repoType);
            data.put("authType", authType);
            data.put("branch", branch);
            data.put("parseOptions", parseOptions);
            data.put("ragId", ragId);
            data.put("knowledgeBaseId", knowledgeBaseId);
            data.put("knowledgeBaseName", knowledgeBaseName);
            data.put("knowledgeName", knowledgeName);
            if (contentsResp.getStatusCode() == HttpStatus.OK) {
                data.put("files", contentsResp.getBody());
                data.put("fileCount", contentsResp.getBody() != null ? contentsResp.getBody().size() : 0);
            } else {
                data.put("files", List.of());
                data.put("fileCount", 0);
            }

            @SuppressWarnings("unchecked") List<String> po = (List<String>) parseOptions;
            IngestResult r = gitIngestService.ingest(repoUrl, branch, po,
                    firstNonEmpty(knowledgeName, knowledgeBaseName), ragId, baseDir);
            Map<String, Object> ingestData = new HashMap<>();
            ingestData.put("clonedPath", r.getClonedPath());
            ingestData.put("ingestedDocuments", r.getDocuments());
            ingestData.put("ingestedChunks", r.getChunks());
            ingestData.put("ingestedKnowledge", r.getKnowledge());
            ingestData.put("ingestedRagId", r.getRagId());
            data.put("ingest", ingestData);

            Map<String, Object> response = new HashMap<>();
            response.put("code", "0000");
            response.put("info", "仓库解析并入库成功");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("解析并入库异常：{}", e.getMessage(), e);
            return error("解析并入库失败：" + e.getMessage());
        }
    }

    private static String asString(Object o) { return o == null ? null : String.valueOf(o); }
    private static String asStringOrDefault(Object o, String def) {
        String s = asString(o); return (s == null || s.isEmpty()) ? def : s;
    }
    private static String firstNonEmpty(String... arr) {
        for (String s : arr) if (s != null && !s.isEmpty()) return s; return null;
    }

    private ResponseEntity<Map<String, Object>> error(String msg) {
        Map<String, Object> r = new HashMap<>();
        r.put("code", "5000");
        r.put("info", msg);
        r.put("data", null);
        return ResponseEntity.status(500).body(r);
    }
}
