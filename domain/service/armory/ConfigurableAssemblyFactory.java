package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.model.valobj.*;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 配置化组装工厂
 * 替代硬编码的ChatClient组装逻辑
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Component
public class ConfigurableAssemblyFactory extends AbstractArmorySupport {

    /**
     * 根据配置动态创建ChatClient
     * 替代AiClientNode中的硬编码组装逻辑
     */
    public ChatClient createChatClient(AiClientVO clientVO, 
                                     Map<Long, AiClientSystemPromptVO> systemPromptMap,
                                     Map<String, Object> assemblyConfig) {
        log.info("配置化创建ChatClient，客户端ID: {}", clientVO.getClientId());
        
        // 1. 获取系统提示词（从配置中读取，替代硬编码）
        String defaultSystem = getSystemPrompt(clientVO, systemPromptMap, assemblyConfig);
        
        // 2. 获取聊天模型
        OpenAiChatModel chatModel = getChatModel(clientVO, assemblyConfig);
        
        // 3. 获取工具回调提供者（从配置中读取）
        SyncMcpToolCallbackProvider toolCallbackProvider = getToolCallbackProvider(clientVO, assemblyConfig);
        
        // 4. 获取顾问数组（从配置中读取）
        Advisor[] advisors = getAdvisors(clientVO, assemblyConfig);
        
        // 5. 根据配置动态构建ChatClient
        return buildChatClient(chatModel, defaultSystem, toolCallbackProvider, advisors, assemblyConfig);
    }
    
    /**
     * 获取系统提示词（替代硬编码的"AI 智能体"）
     */
    private String getSystemPrompt(AiClientVO clientVO, 
                                 Map<Long, AiClientSystemPromptVO> systemPromptMap,
                                 Map<String, Object> assemblyConfig) {
        // 优先从配置中获取默认提示词
        String defaultPrompt = (String) assemblyConfig.get("defaultSystemPrompt");
        if (defaultPrompt != null) {
            return defaultPrompt;
        }
        
        // 从数据库配置中获取
        AiClientSystemPromptVO systemPrompt = systemPromptMap.get(clientVO.getSystemPromptId());
        if (systemPrompt != null) {
            return systemPrompt.getPromptContent();
        }
        
        // 最后的默认值
        return "AI 智能体";
    }
    
    /**
     * 获取聊天模型
     */
    private OpenAiChatModel getChatModel(AiClientVO clientVO, Map<String, Object> assemblyConfig) {
        // 从Spring容器中获取已注册的模型Bean
        String modelBeanName = clientVO.getModelBeanName();
        if (modelBeanName != null) {
            return getBean(modelBeanName);
        }
        return null;
    }
    
    /**
     * 获取工具回调提供者
     */
    private SyncMcpToolCallbackProvider getToolCallbackProvider(AiClientVO clientVO, Map<String, Object> assemblyConfig) {
        // 根据配置创建工具回调提供者
        List<McpSyncClient> mcpSyncClients = new ArrayList<>();
        List<String> mcpBeanNameList = clientVO.getMcpBeanNameList();
        if (mcpBeanNameList != null) {
            for (String mcpBeanName : mcpBeanNameList) {
                McpSyncClient mcpSyncClient = getBean(mcpBeanName);
                if (mcpSyncClient != null) {
                    mcpSyncClients.add(mcpSyncClient);
                }
            }
        }
        
        if (!mcpSyncClients.isEmpty()) {
            return new SyncMcpToolCallbackProvider(mcpSyncClients.toArray(new McpSyncClient[0]));
        }
        return null;
    }
    
    /**
     * 获取顾问数组
     */
    private Advisor[] getAdvisors(AiClientVO clientVO, Map<String, Object> assemblyConfig) {
        // 根据配置创建顾问数组
        List<Advisor> advisors = new ArrayList<>();
        List<String> advisorBeanNameList = clientVO.getAdvisorBeanNameList();
        if (advisorBeanNameList != null) {
            for (String advisorBeanName : advisorBeanNameList) {
                Advisor advisor = getBean(advisorBeanName);
                if (advisor != null) {
                    advisors.add(advisor);
                }
            }
        }
        return advisors.toArray(new Advisor[0]);
    }
    
    /**
     * 根据配置动态构建ChatClient
     * 替代硬编码的ChatClient.builder()调用
     */
    private ChatClient buildChatClient(OpenAiChatModel chatModel, 
                                     String defaultSystem,
                                     SyncMcpToolCallbackProvider toolCallbackProvider,
                                     Advisor[] advisors,
                                     Map<String, Object> assemblyConfig) {
        
        ChatClient.ChatClientBuilder builder = ChatClient.builder(chatModel);
        
        // 根据配置决定是否设置默认系统提示词
        if (assemblyConfig.getOrDefault("enableDefaultSystem", true).equals(true)) {
            builder.defaultSystem(defaultSystem);
        }
        
        // 根据配置决定是否设置工具回调
        if (assemblyConfig.getOrDefault("enableToolCallbacks", true).equals(true) && toolCallbackProvider != null) {
            builder.defaultToolCallbacks(toolCallbackProvider.getToolCallbacks());
        }
        
        // 根据配置决定是否设置顾问
        if (assemblyConfig.getOrDefault("enableAdvisors", true).equals(true) && advisors != null) {
            builder.defaultAdvisors(advisors);
        }
        
        return builder.build();
    }
}
