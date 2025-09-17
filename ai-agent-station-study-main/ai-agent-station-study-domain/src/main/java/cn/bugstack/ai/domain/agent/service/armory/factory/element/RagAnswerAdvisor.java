package cn.bugstack.ai.domain.agent.service.armory.factory.element;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RagAnswerAdvisor implements BaseAdvisor {

    private final VectorStore vectorStore;
    private final SearchRequest searchRequest;
    private final String userTextAdvise;

    public RagAnswerAdvisor(VectorStore vectorStore, SearchRequest searchRequest) {
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
        this.userTextAdvise = "\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n";
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        HashMap<String, Object> context = new HashMap(chatClientRequest.context());

        // 不再解析和拼接用户输入，统一依赖上游 Prompt/默认查询配置
        SearchRequest searchRequestToUse = SearchRequest.from(this.searchRequest)
                .filterExpression(this.doGetFilterExpression(context))
                .build();
        
        log.info("RagAnswerAdvisor 开始向量检索 - 使用默认/上游查询配置，过滤条件: {}", this.doGetFilterExpression(context));
        log.info("使用的 vectorStore 类型: {}", this.vectorStore.getClass().getSimpleName());
        // 追加：在异步场景下打印实际使用的 Embedding 配置（baseUrl/embeddingsPath/model），便于排查 404 等问题
        try {
            printEmbeddingRuntimeConfig();
        } catch (Exception e) {
            log.warn("无法打印向量模型运行时配置: {}", e.getMessage());
        }
        
        List<Document> documents = this.vectorStore.similaritySearch(searchRequestToUse);
        
        log.info("向量检索完成 - 检索到文档数量: {}", documents != null ? documents.size() : 0);
        context.put("qa_retrieved_documents", documents);

        String documentContext = documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> advisedUserParams = new HashMap(chatClientRequest.context());
        advisedUserParams.put("question_answer_context", documentContext);

        // 不修改原始提示词内容，仅透传并增加向量检索上下文
        return ChatClientRequest.builder()
                .prompt(chatClientRequest.prompt())
                .context(advisedUserParams)
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        ChatResponse.Builder chatResponseBuilder = ChatResponse.builder().from(chatClientResponse.chatResponse());
        chatResponseBuilder.metadata("qa_retrieved_documents", chatClientResponse.context().get("qa_retrieved_documents"));
        ChatResponse chatResponse = chatResponseBuilder.build();

        return ChatClientResponse.builder()
                .chatResponse(chatResponse)
                .context(chatClientResponse.context())
                .build();
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(this.before(chatClientRequest, callAdvisorChain));
        return this.after(chatClientResponse, callAdvisorChain);
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        return BaseAdvisor.super.adviseStream(chatClientRequest, streamAdvisorChain);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    protected Filter.Expression doGetFilterExpression(Map<String, Object> context) {
        return context.containsKey("qa_filter_expression") && StringUtils.hasText(context.get("qa_filter_expression").toString()) ? (new FilterExpressionTextParser()).parse(context.get("qa_filter_expression").toString()) : this.searchRequest.getFilterExpression();
    }

    /**
     * 反射打印当前使用的 Embedding 模型配置（仅在 PgVectorStore + OpenAiEmbeddingModel 可用）
     */
    private void printEmbeddingRuntimeConfig() {
        try {
            // 仅在 PgVectorStore 下尝试获取底层 embeddingModel
            if (!this.vectorStore.getClass().getName().contains("PgVectorStore")) {
                return;
            }

            Object embeddingModel = getPrivateField(this.vectorStore, "embeddingModel");
            if (embeddingModel == null) {
                log.info("未获取到 embeddingModel 运行时实例");
                return;
            }

            String modelClass = embeddingModel.getClass().getName();
            log.info("EmbeddingModel 类型: {}", modelClass);

            // 尝试 OpenAiEmbeddingModel，提取 OpenAiApi 配置
            if (modelClass.endsWith("OpenAiEmbeddingModel")) {
                Object openAiApi = getPrivateField(embeddingModel, "openAiApi");
                if (openAiApi != null) {
                    String baseUrl = String.valueOf(getPrivateField(openAiApi, "baseUrl"));
                    String embeddingsPath = String.valueOf(getPrivateField(openAiApi, "embeddingsPath"));
                    String completionsPath = String.valueOf(getPrivateField(openAiApi, "completionsPath"));
                    log.info("Embedding OpenAI 配置 - baseUrl: {}, embeddingsPath: {}, completionsPath: {}", baseUrl, embeddingsPath, completionsPath);
                }

                // 打印选用的模型名（如果可得）
                Object options = getPrivateField(embeddingModel, "defaultOptions");
                if (options == null) {
                    options = getPrivateField(embeddingModel, "options");
                }
                if (options != null) {
                    Object modelName = invokeGetter(options, "getModel");
                    if (modelName != null) {
                        log.info("Embedding 使用的模型: {}", modelName);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("打印 Embedding 运行时配置异常: {}", e.getMessage());
        }
    }

    private Object getPrivateField(Object target, String fieldName) {
        try {
            Class<?> c = target.getClass();
            while (c != null) {
                try {
                    java.lang.reflect.Field f = c.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f.get(target);
                } catch (NoSuchFieldException ignore) {
                    c = c.getSuperclass();
                }
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private Object invokeGetter(Object target, String getterName) {
        try {
            java.lang.reflect.Method m = target.getClass().getMethod(getterName);
            return m.invoke(target);
        } catch (Exception ignore) {
            return null;
        }
    }

}
