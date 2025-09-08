package ai.agent.config; // 包声明，需与源码所在目录结构一致：src/main/java/ai/agent/config

// —— 下面是所需依赖的导入 ——

// Spring AI：OpenAI 向量嵌入模型（将文本转为向量）
import org.springframework.ai.openai.OpenAiEmbeddingModel;
// Spring AI：OpenAI HTTP 客户端封装（负责调用 OpenAI 接口）
import org.springframework.ai.openai.api.OpenAiApi;
// Spring AI：文本切分工具（按 token 规则把长文本拆分，便于做向量化/召回）
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
// Spring AI：PgVector 的向量存储实现（基于 Postgres + pgvector 插件）
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;

// Spring：用于按名称/配置注入依赖
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
// Spring Boot 条件化装配：只有当某个 Bean 存在时，才创建当前 Bean
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
// Spring：把方法返回对象注册到容器；把类标记为配置类
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Spring JDBC：基于数据源的轻量 SQL 工具，这里用于连接向量库（PgVector 所在的 Postgres）
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * AiAgentConfig
 * 职责：
 * 1）基于 PgVector + OpenAI Embedding 模型，装配一个可注入的向量存储 Bean（vectorStore）。
 * 2）提供一个 TokenTextSplitter Bean，用于把长文本按 token 粒度拆分，便于向量化/召回。
 *
 * 注意：
 * - 该配置依赖一个名为 "pgVectorJdbcTemplate" 的 JdbcTemplate（来自 PgVector 数据源）。
 * - 需要确保向量表（如 vector_store_openai）已经在数据库中创建好。
 */
@Configuration // 声明当前类为 Spring 配置类，容器启动时会扫描其中的 @Bean 定义
public class AiAgentConfig {
    @Bean("vectorStore") // 将方法返回值注册为 Spring Bean，外部可通过 @Resource/@Autowired/@Qualifier 注入
    @ConditionalOnBean(name = "pgVectorJdbcTemplate") // 依赖前置的 PgVector JdbcTemplate（在 DataSourceConfig 中定义）
    public PgVectorStore pgVectorStore(
            // 从配置文件注入 OpenAI 的 Base URL（可对接官方/私有代理等）
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            // 从配置文件或环境变量注入 OpenAI API Key
            @Value("${spring.ai.openai.api-key}") String apiKey,
            // 注入名为 "pgVectorJdbcTemplate" 的 JdbcTemplate（指向 PgVector 数据源）
            @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate) {

        // 构建 OpenAI API 客户端（底层负责鉴权/HTTP 调用等）
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(baseUrl) // 设置 OpenAI 接口的根地址
                .apiKey(apiKey)   // 设置鉴权用的 API Key
                .build();         // 构建实例

        // 基于 OpenAI API 客户端创建嵌入模型（将文本转成稠密向量表示）
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi);

        // 使用 PgVector 的构建器创建向量存储：
        // - 通过 jdbcTemplate 访问 Postgres(pgvector)
        // - 通过 embeddingModel 负责把文本 embed 成向量
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                // 指定向量表名（需保证已按上面的 DDL 创建；如使用其他表名请同步修改）
                .vectorTableName("vector_store_openai")
                .build(); // 返回最终的 PgVectorStore 实例
    }

    /**
     * 定义 TokenTextSplitter Bean：
     * - 用于将长文本按 token 方式切分（避免超过模型的 token 限制）。
     * - 向量化/检索时，通常先切分成片段后，再逐片段做 embedding 与入库。
     */
    @Bean // 注册成 Spring Bean，后续在需要切分文本的地方直接注入使用
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter(); // 使用默认切分配置；如需自定义可扩展构造参数
    }

}
