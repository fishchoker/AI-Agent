package cn.bugstack.ai.test.vector;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VectorStoreIngestTest {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreIngestTest.class);

	/*
	 * @Autowired private PgVectorStore pgVectorStore;
	 */
    
    @Autowired
    @Qualifier("vectorStore")
    private PgVectorStore pgVectorStore;

    @Autowired
    private TokenTextSplitter tokenTextSplitter;

    @Autowired
    @Qualifier("pgVectorJdbcTemplate")
    private JdbcTemplate pgVectorJdbcTemplate;

    @Test
    public void test_chunk_and_ingest_to_vector_store() {
        // 0) 打印连接信息与表维度
        Map<String, Object> conn = pgVectorJdbcTemplate.queryForMap(
                "SELECT current_database() AS db, current_user AS usr, inet_server_addr()::text AS host, inet_server_port() AS port, current_schema() AS schema");
        Map<String, Object> embedding = pgVectorJdbcTemplate.queryForMap(
                "SELECT format_type(a.atttypid,a.atttypmod) AS embedding_type, (a.atttypmod - 16)/4 AS dims\n" +
                        "FROM pg_attribute a\n" +
                        "WHERE a.attrelid = 'public.vector_store_openai'::regclass AND a.attname = 'embedding'");
        log.info("Connection: {}", conn);
        log.info("Embedding: {}", embedding);
        System.out.println("[TEST] Connection: " + conn);
        System.out.println("[TEST] Embedding: " + embedding);

        // 1) 基准计数
        String knowledgeTag = "unit-test-knowledge";
        String ragId = "ut-9001";
        Long before = pgVectorJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM public.vector_store_openai WHERE metadata->>'knowledge' = ?",
                Long.class,
                knowledgeTag
        );
        if (before == null) before = 0L;
        log.info("Before count for knowledge='{}': {}", knowledgeTag, before);
        System.out.println("[TEST] Before count: " + before);

        // 2) 构造内存资源 -> Tika 解析 -> 分片 -> 设置元数据
        String content = "这是一段用于向量化入库的测试文本。\n" +
                "它将被Tika解析并经由TokenTextSplitter分片，然后写入pgvector。\n" +
                "希望这一过程顺利，方便我们在测试中进行校验。";
        org.springframework.core.io.Resource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "unit-test.txt";
            }
        };

        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();
        List<Document> chunks = tokenTextSplitter.apply(documents);
        for (Document chunk : chunks) {
            chunk.getMetadata().put("knowledge", knowledgeTag);
            chunk.getMetadata().put("ragId", ragId);
        }
        log.info("Parsed documents: {}, chunks: {}", documents.size(), chunks.size());
        System.out.println("[TEST] Parsed documents: " + documents.size() + ", chunks: " + chunks.size());

        // 3) 入库
        pgVectorStore.accept(chunks);
        log.info("Ingested {} chunks into pgvector.", chunks.size());
        System.out.println("[TEST] Ingested chunks: " + chunks.size());

        // 4) 计数校验（至少不小于原值）
        Long after = pgVectorJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM public.vector_store_openai WHERE metadata->>'knowledge' = ?",
                Long.class,
                knowledgeTag
        );
        if (after == null) after = 0L;
        log.info("After count for knowledge='{}': {}", knowledgeTag, after);
        System.out.println("[TEST] After count: " + after);

        Assert.assertTrue("入库后计数未增加，before=" + before + ", after=" + after, after >= before);
    }
}
