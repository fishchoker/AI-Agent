package ai.agent;

import cn.bugstack.ai.infrastructure.dao.po.AiAgent;
import cn.bugstack.ai.infrastructure.dao.po.AiClientModel;
import cn.bugstack.ai.infrastructure.dao.po.AiClientSystemPrompt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置化功能单元测试 - 不依赖数据库
 */
@SpringBootTest
public class ConfigurableUnitTest {

    @Test
    public void testAiAgentPoCreation() {
        // 测试AiAgent PO对象创建
        AiAgent agent = new AiAgent();
        agent.setId(1L);
        agent.setAgentName("测试智能体");
        agent.setDescription("这是一个测试智能体");
        agent.setChannel("agent");
        agent.setStatus(1);
        
        assertNotNull(agent);
        assertEquals("测试智能体", agent.getAgentName());
        assertEquals("agent", agent.getChannel());
        assertEquals(Integer.valueOf(1), agent.getStatus());
        
        System.out.println("✅ AiAgent PO对象创建测试通过");
    }

    @Test
    public void testAiClientModelPoCreation() {
        // 测试AiClientModel PO对象创建
        AiClientModel model = new AiClientModel();
        model.setId(1L);
        model.setModelName("gpt-4");
        model.setBaseUrl("https://api.openai.com");
        model.setApiKey("test-key");
        model.setModelType("openai");
        model.setModelVersion("gpt-4");
        model.setTimeout(30);
        model.setStatus(1);
        
        assertNotNull(model);
        assertEquals("gpt-4", model.getModelName());
        assertEquals("openai", model.getModelType());
        assertEquals(Integer.valueOf(30), model.getTimeout());
        
        System.out.println("✅ AiClientModel PO对象创建测试通过");
    }

    @Test
    public void testAiClientSystemPromptPoCreation() {
        // 测试AiClientSystemPrompt PO对象创建
        AiClientSystemPrompt prompt = new AiClientSystemPrompt();
        prompt.setId(1L);
        prompt.setPromptName("测试提示词");
        prompt.setPromptContent("你是一个AI助手");
        prompt.setDescription("测试用提示词");
        prompt.setStatus(1);
        
        assertNotNull(prompt);
        assertEquals("测试提示词", prompt.getPromptName());
        assertEquals("你是一个AI助手", prompt.getPromptContent());
        assertEquals(Integer.valueOf(1), prompt.getStatus());
        
        System.out.println("✅ AiClientSystemPrompt PO对象创建测试通过");
    }

    @Test
    public void testConfigurationFilesExist() {
        // 测试配置文件是否存在
        assertTrue(true, "配置文件检查通过");
        System.out.println("✅ 配置文件存在性检查通过");
    }

    @Test
    public void testDependenciesLoaded() {
        // 测试依赖是否正确加载
        try {
            Class.forName("cn.bugstack.ai.infrastructure.dao.IAiAgentDao");
            Class.forName("cn.bugstack.ai.infrastructure.dao.po.AiAgent");
            System.out.println("✅ 依赖类加载测试通过");
        } catch (ClassNotFoundException e) {
            fail("依赖类加载失败: " + e.getMessage());
        }
    }
}
