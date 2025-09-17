package cn.bugstack.ai.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/upload/")
public class UploadController {

	@Resource
	private PgVectorStore pgVectorStore;

	@Resource
	private TokenTextSplitter tokenTextSplitter;

	@Resource(name = "pgVectorJdbcTemplate")
	private JdbcTemplate pgVectorJdbcTemplate;

	/**
	 * 批量上传文件：解析 -> 分片 -> 设置元数据(knowledge) -> 校验表维度 -> 向量化入库
	 */
	@RequestMapping(value = "batch", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> batchUpload(
			@RequestParam("files") MultipartFile[] files,
			@RequestParam(value = "knowledgeTag", required = false) String knowledgeTag,
			@RequestParam(value = "ragId", required = false) String ragId) {
		try {
			List<Map<String, Object>> fileInfos = new ArrayList<>();
			List<Document> allDocuments = new ArrayList<>();

			if (files != null) {
				for (MultipartFile file : files) {
					Map<String, Object> info = new HashMap<>();
					info.put("filename", file.getOriginalFilename());
					info.put("size", file.getSize());
					fileInfos.add(info);

					// 1) 构造 Resource 给 Tika 解析（使用全限定名避免与注解 @Resource 冲突）
					org.springframework.core.io.Resource resource = new ByteArrayResource(file.getBytes()) {
						@Override
						public String getFilename() {
							return file.getOriginalFilename();
						}
					};

					// 2) Tika 读取为 Document 列表
					TikaDocumentReader reader = new TikaDocumentReader(resource);
					List<Document> documents = reader.get();

					// 3) 分片
					List<Document> chunks = tokenTextSplitter.apply(documents);

					// 4) 设置 knowledge 元数据（优先知识库参数，其次文件名）
					String knowledge = (knowledgeTag != null && !knowledgeTag.isEmpty())
							? knowledgeTag
							: Optional.ofNullable(file.getOriginalFilename()).orElse("default-knowledge");
					for (Document chunk : chunks) {
						chunk.getMetadata().put("knowledge", knowledge);
						if (ragId != null && !ragId.isEmpty()) {
							chunk.getMetadata().put("ragId", ragId);
						}
					}

					allDocuments.addAll(chunks);
				}
			}

			// 5) 入库前：校验当前连接与目标表维度（解析 format_type 括号数字为真实维度）
			Map<String, Object> conn = pgVectorJdbcTemplate.queryForMap(
					"SELECT current_database() AS db, current_user AS usr, inet_server_addr()::text AS host, inet_server_port() AS port, current_schema() AS schema"
			);
			Map<String, Object> embeddingInfo = pgVectorJdbcTemplate.queryForMap(
					"SELECT format_type(a.atttypid,a.atttypmod) AS embedding_type, " +
							"NULLIF(substring(format_type(a.atttypid,a.atttypmod) FROM '\\((\\d+)\\)'), '')::int AS dims_parsed\n" +
							"FROM pg_attribute a\n" +
							"WHERE a.attrelid = 'public.vector_store_openai'::regclass AND a.attname = 'embedding'"
			);
			Integer dims = embeddingInfo.get("dims_parsed") == null ? null : Integer.valueOf(embeddingInfo.get("dims_parsed").toString());

			Map<String, Object> data = new HashMap<>();
			data.put("uploaded", fileInfos);
			data.put("knowledgeTag", knowledgeTag);
			data.put("ragId", ragId);
			data.put("documents", allDocuments.size());
			data.put("connection", conn);
			data.put("embedding", embeddingInfo);

			// 若维度不为 2048，直接返回提示，不入库
			if (dims == null || dims != 2048) {
				Map<String, Object> resp = new HashMap<>();
				resp.put("code", "0002");
				resp.put("info", "向量表维度与当前嵌入不一致，已取消入库。请将 public.vector_store_openai.embedding 调整为 vector(2048) 后重试。");
				resp.put("data", data);
				return ResponseEntity.ok(resp);
			}

			// 6) 入库
			int chunkCount = allDocuments.size();
			if (!allDocuments.isEmpty()) {
				pgVectorStore.accept(allDocuments);
			}
			data.put("chunks", chunkCount);

			Map<String, Object> resp = new HashMap<>();
			resp.put("code", "0000");
			resp.put("info", allDocuments.isEmpty() ? "未解析到文档" : "上传并入库成功");
			resp.put("data", data);
			return ResponseEntity.ok(resp);
		} catch (Exception e) {
			log.error("批量上传处理异常：{}", e.getMessage(), e);
			Map<String, Object> resp = new HashMap<>();
			resp.put("code", "5000");
			resp.put("info", "上传失败：" + e.getMessage());
			resp.put("data", null);
			return ResponseEntity.status(500).body(resp);
		}
	}
}
