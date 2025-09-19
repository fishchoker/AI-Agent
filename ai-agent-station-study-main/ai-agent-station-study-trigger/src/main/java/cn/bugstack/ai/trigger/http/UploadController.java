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
import cn.bugstack.ai.infrastructure.dao.IAiClientRagOrderDao;
import cn.bugstack.ai.infrastructure.dao.po.AiClientRagOrder;
import org.springframework.context.ApplicationContext;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/ai/upload/")
public class UploadController {

	@Resource
	private PgVectorStore pgVectorStore;
	
	// 动态获取PgVectorStore Bean
	private PgVectorStore getDynamicPgVectorStore() {
		try {
			// 尝试获取动态装配的PgVectorStore Bean
			// 动态Bean名称格式：vectorStore_{apiId}
			String[] beanNames = applicationContext.getBeanNamesForType(PgVectorStore.class);
			log.info("找到所有PgVectorStore Bean: {}", java.util.Arrays.toString(beanNames));
			
			for (String beanName : beanNames) {
				if (beanName.startsWith("vectorStore_")) {
					log.info("使用动态装配的PgVectorStore Bean: {}", beanName);
					PgVectorStore vectorStore = applicationContext.getBean(beanName, PgVectorStore.class);
					log.info("成功获取动态PgVectorStore Bean: {}, 类型: {}", beanName, vectorStore.getClass().getName());
					return vectorStore;
				}
			}
			// 如果没有找到动态Bean，使用默认的
			log.info("未找到动态装配的PgVectorStore，使用默认Bean");
			return pgVectorStore;
		} catch (Exception e) {
			log.warn("获取动态PgVectorStore失败，使用默认Bean: {}", e.getMessage(), e);
			return pgVectorStore;
		}
	}

	@Resource
	private TokenTextSplitter tokenTextSplitter;

	@Resource(name = "pgVectorJdbcTemplate")
	private JdbcTemplate pgVectorJdbcTemplate;

	@Resource
	private IAiClientRagOrderDao aiClientRagOrderDao;

	@Resource
	private ApplicationContext applicationContext;

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

					// 4) 设置 knowledge 元数据
					String knowledge = determineKnowledgeValue(knowledgeTag, ragId, file.getOriginalFilename());
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
				log.info("开始向量化入库，文档数量: {}", chunkCount);
				
				// 优先使用动态注册的 PgVectorStore（存在则选择以 vectorStore_ 开头的 Bean），否则回退到默认 Bean
				PgVectorStore vectorStore = getDynamicPgVectorStore();
				log.info("使用PgVectorStore实例: {}", vectorStore.getClass().getName());
				try {
					log.info("调用PgVectorStore.accept()开始...");
					vectorStore.accept(allDocuments);
					log.info("PgVectorStore.accept()完成");
				} catch (Exception e) {
					log.error("PgVectorStore.accept()失败: {}", e.getMessage(), e);
					throw e;
				}
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

	/**
	 * 确定knowledge值
	 * 优先级：knowledgeTag > ragId对应的rag_name > 文件名 > 默认值
	 */
	private String determineKnowledgeValue(String knowledgeTag, String ragId, String fileName) {
		// 1. 优先使用knowledgeTag
		if (knowledgeTag != null && !knowledgeTag.trim().isEmpty()) {
			return knowledgeTag.trim();
		}
		
		// 2. 如果有ragId，查询对应的rag_name
		if (ragId != null && !ragId.trim().isEmpty()) {
			try {
				AiClientRagOrder ragOrder = aiClientRagOrderDao.queryByRagId(ragId.trim());
				if (ragOrder != null && ragOrder.getRagName() != null && !ragOrder.getRagName().trim().isEmpty()) {
					log.info("使用ragId对应的rag_name作为knowledge: ragId={}, ragName={}", ragId, ragOrder.getRagName());
					return ragOrder.getRagName().trim();
				}
			} catch (Exception e) {
				log.warn("查询ragId对应的rag_name失败: ragId={}, error={}", ragId, e.getMessage());
			}
		}
		
		// 3. 使用文件名
		if (fileName != null && !fileName.trim().isEmpty()) {
			return fileName.trim();
		}
		
		// 4. 默认值
		return "default-knowledge";
	}

}
