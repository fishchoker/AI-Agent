package cn.bugstack.ai.domain.agent.service.armory;

import cn.bugstack.ai.domain.agent.model.entity.ArmoryCommandEntity;
import cn.bugstack.ai.domain.agent.model.valobj.enums.AiAgentEnumVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientSystemPromptVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientVO;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson.JSON;
import io.modelcontextprotocol.client.McpSyncClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ai agent 客户端对话对象节点
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/19 09:17
 */
@Slf4j
@Service
public class AiClientNode extends AbstractArmorySupport {

    @Override
    protected String doApply(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建节点，客户端{}", JSON.toJSONString(requestParameter));

        List<AiClientVO> aiClientList = dynamicContext.getValue(dataName());

        if (null == aiClientList || aiClientList.isEmpty()) {
            return router(requestParameter, dynamicContext);
        }

        Map<String, AiClientSystemPromptVO> systemPromptMap = dynamicContext.getValue(AiAgentEnumVO.AI_CLIENT_SYSTEM_PROMPT.getDataName());
        if (systemPromptMap == null) {
            systemPromptMap = new HashMap<>();
            log.warn("系统提示词映射为空，将使用默认提示词");
        }

        for (AiClientVO aiClientVO : aiClientList) {
            // 1. 预设话术
            StringBuilder defaultSystem = new StringBuilder("Ai 智能体 \r\n");
            List<String> promptIdList = aiClientVO.getPromptIdList();
            if (promptIdList != null && !promptIdList.isEmpty()) {
                for (String promptId : promptIdList) {
                    AiClientSystemPromptVO aiClientSystemPromptVO = systemPromptMap.get(promptId);
                    if (aiClientSystemPromptVO != null && aiClientSystemPromptVO.getPromptContent() != null) {
                        defaultSystem.append(aiClientSystemPromptVO.getPromptContent());
                    } else {
                        log.warn("客户端 {} 引用的系统提示词 {} 未找到或内容为空，跳过", aiClientVO.getClientId(), promptId);
                    }
                }
            }

            // 2. 对话模型
            OpenAiChatModel chatModel;
            try {
                chatModel = getBean(aiClientVO.getModelBeanName());
                if (chatModel == null) {
                    log.error("客户端 {} 引用的模型 {} 未找到，跳过该客户端", aiClientVO.getClientId(), aiClientVO.getModelBeanName());
                    continue;
                }
            } catch (Exception e) {
                log.error("客户端 {} 引用的模型 {} 加载失败: {}，跳过该客户端", aiClientVO.getClientId(), aiClientVO.getModelBeanName(), e.getMessage());
                continue;
            }

            // 3. MCP 服务
            List<McpSyncClient> mcpSyncClients = new ArrayList<>();
            List<String> mcpBeanNameList = aiClientVO.getMcpBeanNameList();
            if (mcpBeanNameList != null && !mcpBeanNameList.isEmpty()) {
                for (String mcpBeanName : mcpBeanNameList) {
                    try {
                        McpSyncClient mcpSyncClient = getBean(mcpBeanName);
                        if (mcpSyncClient != null) {
                            mcpSyncClients.add(mcpSyncClient);
                        } else {
                            log.warn("客户端 {} 引用的MCP服务 {} 未找到，跳过", aiClientVO.getClientId(), mcpBeanName);
                        }
                    } catch (Exception e) {
                        log.warn("客户端 {} 引用的MCP服务 {} 加载失败: {}", aiClientVO.getClientId(), mcpBeanName, e.getMessage());
                    }
                }
            }

            // 4. advisor 顾问角色
            List<Advisor> advisors = new ArrayList<>();
            List<String> advisorBeanNameList = aiClientVO.getAdvisorBeanNameList();
            if (advisorBeanNameList != null && !advisorBeanNameList.isEmpty()) {
                for (String advisorBeanName : advisorBeanNameList) {
                    try {
                        Advisor advisor = getBean(advisorBeanName);
                        if (advisor != null) {
                            advisors.add(advisor);
                        } else {
                            log.warn("客户端 {} 引用的顾问 {} 未找到，跳过", aiClientVO.getClientId(), advisorBeanName);
                        }
                    } catch (Exception e) {
                        log.warn("客户端 {} 引用的顾问 {} 加载失败: {}", aiClientVO.getClientId(), advisorBeanName, e.getMessage());
                    }
                }
            }

            Advisor[] advisorArray = advisors.toArray(new Advisor[]{});

            // 5. 构建对话客户端
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultSystem(defaultSystem.toString())
                    .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients.toArray(new McpSyncClient[]{})))
                    .defaultAdvisors(advisorArray)
                    .build();

            registerBean(beanName(aiClientVO.getClientId()), ChatClient.class, chatClient);
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<ArmoryCommandEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(ArmoryCommandEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

    @Override
    protected String beanName(String id) {
        return AiAgentEnumVO.AI_CLIENT.getBeanName(id);
    }

    @Override
    protected String dataName() {
        return AiAgentEnumVO.AI_CLIENT.getDataName();
    }

}
