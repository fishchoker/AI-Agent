package cn.bugstack.ai.test.spring.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiGithubMCPTest {

    private ChatModel chatModel;
    private ChatClient githubChatClient;

    @Before
    public void init() {
        // OpenAI API 配置
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://open.bigmodel.cn/api/paas/")
                .apiKey("sk-c259c3bbdeb449e7b0673c8f393006ab.iPaHfYyhLddTiOw5")
                .completionsPath("v4/chat/completions")
                .embeddingsPath("v4/embeddings")
                .build();

        chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("glm-4.5")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(stdioMcpClient_github()).getToolCallbacks())
                        .maxTokens(4000)
                        .build())
                .build();

        githubChatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是一个 GitHub 智能助手，名为 GitHub Agent。
                        你可以通过 MCP GitHub 工具访问仓库、issues、pull requests 等信息。
                        
                        ## 职责
                        - 帮助用户查询 GitHub 仓库信息
                        - 分析 issues、PRs 等开发协作数据
                        - 提供结构化的结果输出
                        
                        ## 输出要求
                        请严格按照以下格式输出：
                        
                        **🔎 GitHub 查询结果**
                        - 仓库: [仓库名]
                        - 描述: [仓库描述]
                        - Star 数: [数量]
                        - Fork 数: [数量]
                        
                        **📋 说明**
                        - [补充说明或分析结果]
                        """)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(
                                MessageWindowChatMemory.builder()
                                        .maxMessages(20)
                                        .build()
                        ).build()
                )
                .build();
    }

    @Test
    public void test_queryGithubRepos() {
        String userRequest = "帮我查询fishchoker我在 GitHub 上有哪些仓库";

        log.info("=== GitHub MCP 测试开始 ===");
        log.info("用户请求: {}", userRequest);

        try {
            String result = githubChatClient.prompt()
                    .user(userRequest)
                    .call()
                    .content();

            log.info("GitHub MCP 测试结果: {}", result);
        } catch (Exception e) {
            log.error("执行 GitHub MCP 测试失败", e);
        }

        log.info("=== GitHub MCP 测试结束 ===");
    }

    /**
     * 初始化 GitHub MCP 的 stdio 客户端
     */
    public McpSyncClient stdioMcpClient_github() {
        // Windows 下用 cmd /c npx
        ServerParameters params = ServerParameters.builder("cmd")
                .args(
                        "/c",
                        "npx", "-y",
                        "@smithery/cli@latest",
                        "run",
                        "@smithery-ai/github",
                        "--key", "4faab5a2-b1d5-494e-80bb-9f8902f728de",
                        "--profile", "successful-caterpillar-qvfDEa"
                )
                .build();

        StdioClientTransport transport = new StdioClientTransport(params);

        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofMinutes(180))
                .build();

        var init = mcpSyncClient.initialize();
        log.info("GitHub MCP STDIO Initialized: {}", init);

        return mcpSyncClient;
    }

	/*
	 * //创建仓库测试
	 * 
	 * @Test public void test_createGithubRepo() { String userRequest = """ 使用
	 * GitHub MCP 工具，调用 createRepository 接口。 参数： - 名称: test-mcp-repo - 描述: 这是一个通过
	 * MCP 测试创建的仓库 - 是否私有: false """;
	 * 
	 * log.info("=== GitHub MCP 创建仓库测试 ===");
	 * 
	 * try { String result = githubChatClient.prompt() .user(userRequest) .call()
	 * .content();
	 * 
	 * log.info("创建仓库结果: {}", result); } catch (Exception e) { log.error("创建仓库失败",
	 * e); }
	 * 
	 * log.info("=== GitHub MCP 测试结束 ==="); }
	 */

    //查询pull requests
    @Test
    public void test_listPullRequests() {
        String userRequest = """
            使用 GitHub MCP 工具，调用 listPullRequests 接口。
            参数：
            - 用户名: fishchoker
            - 仓库名: test-mcp-repo
            - 状态: open
            """;

        log.info("=== GitHub MCP 查询 PR 测试 ===");

        try {
            String result = githubChatClient.prompt()
                    .user(userRequest)
                    .call()
                    .content();

            log.info("查询 Pull Requests 结果: {}", result);
        } catch (Exception e) {
            log.error("查询 PR 失败", e);
        }
    }


    //创建pull request
    @Test
    public void test_createPullRequest() {
        String userRequest = """
            使用 GitHub MCP 工具，调用 createPullRequest 接口。
            参数：
            - 用户名: fishchoker
            - 仓库名: test-mcp-repo
            - 源分支: feature-branch
            - 目标分支: main
            - 标题: "MCP Test PR"
            - 描述: "这是一个通过 MCP 创建的测试 Pull Request"
            """;

        log.info("=== GitHub MCP 创建 PR 测试 ===");

        try {
            String result = githubChatClient.prompt()
                    .user(userRequest)
                    .call()
                    .content();

            log.info("创建 Pull Request 结果: {}", result);
        } catch (Exception e) {
            log.error("创建 PR 失败", e);
        }
    }

 // 删除仓库
    @Test
    public void test_deleteGithubRepo() {
        String userRequest = """
            使用 GitHub MCP 工具，调用 deleteRepository 接口。
            参数：
            - 用户名: fishchoker
            - 仓库名: test-mcp-repo
            """;

        log.info("=== GitHub MCP 删除仓库测试 ===");

        try {
            String result = githubChatClient.prompt()
                    .user(userRequest)
                    .call()
                    .content();

            log.info("删除仓库结果: {}", result);
        } catch (Exception e) {
            log.error("删除仓库失败", e);
        }

        log.info("=== GitHub MCP 测试结束 ===");
    }


}
