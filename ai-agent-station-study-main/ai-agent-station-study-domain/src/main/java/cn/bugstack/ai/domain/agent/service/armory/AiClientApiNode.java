package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.model.entity.ArmoryCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.enums.AiAgentEnumVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientApiVO;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * OpenAI API配置节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/1 07:09
 */
@Slf4j
@Service
public class AiClientApiNode extends AbstractArmorySupport {

    @Resource
    private AiClientToolMcpNode aiClientToolMcpNode;
    
    @Autowired
    @Qualifier("pgVectorJdbcTemplate")
    private JdbcTemplate jdbcTemplate;


    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建节点，API 接口请求{}", JSON.toJSONString(requestParameter));

        List<AiClientApiVO> aiClientApiList = dynamicContext.getValue(dataName());

        if (aiClientApiList == null || aiClientApiList.isEmpty()) {
            log.warn("没有需要被初始化的 ai client api");
            return router(requestParameter, dynamicContext);
        }

        for (AiClientApiVO aiClientApiVO : aiClientApiList) {
            // 构建 OpenAiApi
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(aiClientApiVO.getBaseUrl())
                    .apiKey(aiClientApiVO.getApiKey())
                    .completionsPath(aiClientApiVO.getCompletionsPath())
                    .embeddingsPath(aiClientApiVO.getEmbeddingsPath())
                    .build();
			
			  // 打印 baseUrl 和 completionsPath
			  log.info("组装结果 - baseUrl: {}, completionsPath: {}",
			  aiClientApiVO.getBaseUrl(), aiClientApiVO.getCompletionsPath());
			 

            // 注册 OpenAiApi Bean 对象
            registerBean(beanName(aiClientApiVO.getApiId()), OpenAiApi.class, openAiApi);
            
            log.info("Ai Agent 构建节点，构建向量模型 - API ID: {}", aiClientApiVO.getApiId());
            // 2. 构建 Embedding Model
            OpenAiEmbeddingOptions options = OpenAiEmbeddingOptions.builder()
            	    .model("embedding-3")  // 指定模型名
            	    .build();
            OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi,MetadataMode.EMBED,options);
            
            // 快速测试向量模型
            try {
                float[] vector = embeddingModel.embed("向量模型测试");   // 返回单条文本的向量
                log.info("✅ 向量模型可用，向量维度 = {}", vector.length);
            } catch (Exception ex) {
                log.error("❌ 向量模型调用失败: {}", ex.getMessage(), ex);
            }



            // 3. 构建 PgVectorStore
			/*
			 * PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate,
			 * embeddingModel) .vectorTableName("vector_store_" + apiVO.getApiId()) // 动态命名
			 * .build();
			 */
            PgVectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, embeddingModel)
                    .vectorTableName("vector_store_openai") // 固定表名
                    .build();

            // 4. 注册 Bean
            registerBean("vectorStore", PgVectorStore.class, vectorStore);
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity armoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return aiClientToolMcpNode;
    }

    @Override
    protected String beanName(String beanId) {
        return AiAgentEnumVO.AI_CLIENT_API.getBeanName(beanId);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT_API.getDataName();
    }

}
