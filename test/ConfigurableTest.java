package cn.bugstack.ai.test;

import cn.bugstack.ai.domain.agent.service.armory.ConfigurableAssemblyFactory;
import cn.bugstack.ai.domain.agent.service.template.PromptTemplateService;
import cn.bugstack.ai.domain.agent.service.strategy.AdvisorStrategyService;
import cn.bugstack.ai.domain.agent.service.callback.ToolCallbackService;
import cn.bugstack.ai.domain.agent.model.valobj.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置化改造测试类
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@SpringBootTest
@ActiveProfiles("test")
public class ConfigurableTest {

    @Resource
    private ConfigurableAssemblyFactory assemblyFactory;
    
    @Resource
    private PromptTemplateService promptTemplateService;
    
    @Resource
    private AdvisorStrategyService advisorStrategyService;
    
    @Resource
    private ToolCallbackService toolCallbackService;

    @Test
    public void testPromptTemplateService() {
        System.out.println("测试提示词模板服务...");
        
        // 测试RAG提示词模板
        Map<String, Object> variables = new HashMap<>();
        variables.put("documents", "测试文档内容");
        
        String ragPrompt = promptTemplateService.getRagPromptTemplate(variables);
        System.out.println("RAG提示词: " + ragPrompt);
        
        // 测试默认提示词模板
        String defaultPrompt = promptTemplateService.getDefaultPromptTemplate(variables);
        System.out.println("默认提示词: " + defaultPrompt);
        
        System.out.println("提示词模板服务测试完成！");
    }

    @Test
    public void testAdvisorStrategyService() {
        System.out.println("测试顾问策略服务...");
        
        Map<String, Object> config = new HashMap<>();
        config.put("maxMessages", 200);
        
        // 测试创建顾问
        var advisor = advisorStrategyService.createAdvisorByType("PromptChatMemory", config);
        if (advisor != null) {
            System.out.println("成功创建顾问: " + advisor.getClass().getSimpleName());
        } else {
            System.out.println("顾问创建失败或未找到配置");
        }
        
        System.out.println("顾问策略服务测试完成！");
    }

    @Test
    public void testToolCallbackService() {
        System.out.println("测试工具回调服务...");
        
        // 测试创建工具回调提供者
        var provider = toolCallbackService.createToolCallbackProviderByType("mcp", null);
        if (provider != null) {
            System.out.println("成功创建工具回调提供者: " + provider.getClass().getSimpleName());
        } else {
            System.out.println("工具回调提供者创建失败或未找到配置");
        }
        
        System.out.println("工具回调服务测试完成！");
    }

    @Test
    public void testConfigurableAssemblyFactory() {
        System.out.println("测试配置化组装工厂...");
        
        // 创建测试数据
        AiClientVO clientVO = new AiClientVO();
        clientVO.setClientId(1L);
        clientVO.setSystemPromptId(1L);
        clientVO.setModelBeanName("AiClientModel_1");
        clientVO.setMcpBeanNameList(java.util.Arrays.asList("AiClientToolMcp_1"));
        clientVO.setAdvisorBeanNameList(java.util.Arrays.asList("AiClientAdvisor_1"));
        
        Map<Long, AiClientSystemPromptVO> systemPromptMap = new HashMap<>();
        AiClientSystemPromptVO systemPrompt = new AiClientSystemPromptVO();
        systemPrompt.setId(1L);
        systemPrompt.setPromptContent("你是一个测试AI助手");
        systemPromptMap.put(1L, systemPrompt);
        
        Map<String, Object> assemblyConfig = new HashMap<>();
        assemblyConfig.put("enableDefaultSystem", true);
        assemblyConfig.put("enableToolCallbacks", true);
        assemblyConfig.put("enableAdvisors", true);
        assemblyConfig.put("defaultSystemPrompt", "你是一个配置化的AI助手");
        
        try {
            // 测试创建ChatClient
            var chatClient = assemblyFactory.createChatClient(clientVO, systemPromptMap, assemblyConfig);
            if (chatClient != null) {
                System.out.println("成功创建ChatClient: " + chatClient.getClass().getSimpleName());
            } else {
                System.out.println("ChatClient创建失败");
            }
        } catch (Exception e) {
            System.out.println("ChatClient创建异常: " + e.getMessage());
        }
        
        System.out.println("配置化组装工厂测试完成！");
    }

    @Test
    public void testAllServices() {
        System.out.println("==========================================");
        System.out.println("开始综合测试所有配置化服务...");
        System.out.println("==========================================");
        
        testPromptTemplateService();
        System.out.println();
        
        testAdvisorStrategyService();
        System.out.println();
        
        testToolCallbackService();
        System.out.println();
        
        testConfigurableAssemblyFactory();
        System.out.println();
        
        System.out.println("==========================================");
        System.out.println("所有配置化服务测试完成！");
        System.out.println("==========================================");
    }
}
