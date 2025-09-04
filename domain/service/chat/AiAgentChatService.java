package cn.bugstack.ai.domain.agent.service.chat;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.service.IAiAgentChatService;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.ai.domain.agent.service.template.PromptTemplateService;
import cn.bugstack.ai.infrastructure.vector.VectorStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI Agent聊天服务 - 配置化改造版本
 * 
 * 原文件：ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/chat/AiAgentChatService.java
 * 主要改动：使用配置化的RAG提示词模板替代硬编码
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Service
public class AiAgentChatService implements IAiAgentChatService {

    @Resource
    private IAgentRepository repository;
    @Resource
    private DefaultArmoryStrategyFactory defaultArmoryStrategyFactory;
    @Resource
    private VectorStore vectorStore;
    @Resource
    private PromptTemplateService promptTemplateService;

    @Override
    public String aiAgentChat(Long aiAgentId, String message) {
        log.info("智能体对话请求，参数 aiAgentId {} message {}", aiAgentId, message);

        // 查询模型ID
        Long modelId = repository.queryAiClientModelIdByAgentId(aiAgentId);

        // 获取对话模型
        ChatModel chatModel = defaultArmoryStrategyFactory.chatModel(modelId);

        // 封装请求参数
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();
        messages.add(new org.springframework.ai.chat.messages.UserMessage(message));

        ChatResponse response = chatModel.call(Prompt.builder()
                .messages(messages)
                .build());

        String content = response.getResult().getOutput().getContent();
        log.info("智能体对话响应，参数 aiAgentId {} content {}", aiAgentId, content);
        return content;
    }

    @Override
    public Flux<ChatResponse> aiAgentChatStream(Long aiAgentId, Long ragId, String message) {
        log.info("智能体对话请求，参数 aiAgentId {} message {}", aiAgentId, message);

        // 查询模型ID
        Long modelId = repository.queryAiClientModelIdByAgentId(aiAgentId);

        // 获取对话模型
        ChatModel chatModel = defaultArmoryStrategyFactory.chatModel(modelId);

        // 封装请求参数
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

        if (null != ragId && 0 != ragId){
            // 查询RAG标签
            String tag = repository.queryRagKnowledgeTag(ragId);

            SearchRequest searchRequest = SearchRequest.builder()
                    .query(message)
                    .topK(5)
                    .filterExpression("knowledge == '" + tag + "'")
                    .build();

            List<Document> documents = vectorStore.similaritySearch(searchRequest);
            String documentCollectors = documents.stream().map(Document::getFormattedContent).collect(Collectors.joining());
            
            // 使用配置化的RAG提示词模板，替代硬编码
            Map<String, Object> variables = Map.of("documents", documentCollectors);
            String ragPromptTemplate = promptTemplateService.getRagPromptTemplate(variables);
            
            org.springframework.ai.chat.messages.Message ragMessage = new SystemPromptTemplate(ragPromptTemplate)
                    .createMessage(variables);

            messages.add(new org.springframework.ai.chat.messages.UserMessage(message));
            messages.add(ragMessage);
        } else {
            messages.add(new org.springframework.ai.chat.messages.UserMessage(message));
        }

        return chatModel.stream(Prompt.builder()
                .messages(messages)
                .build());
    }
}
