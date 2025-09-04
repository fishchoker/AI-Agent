package cn.bugstack.ai.domain.agent.service.armory.node;

import cn.bugstack.ai.domain.agent.model.entity.AiAgentEngineStarterEntity;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientSystemPromptVO;
import cn.bugstack.ai.domain.agent.model.valobj.AiClientVO;
import cn.bugstack.ai.domain.agent.service.armory.AbstractArmorySupport;
import cn.bugstack.ai.domain.agent.service.armory.ConfigurableAssemblyFactory;
import cn.bugstack.ai.domain.agent.service.armory.factory.DefaultArmoryStrategyFactory;
import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * AI客户端对话对象节点 - 配置化改造版本
 * 
 * 原文件：ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/armory/node/AiClientNode.java
 * 主要改动：使用配置化工厂替代硬编码的ChatClient组装逻辑
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Component
public class AiClientNode extends AbstractArmorySupport {

    @Resource
    private ConfigurableAssemblyFactory assemblyFactory;
    
    @Resource
    private IAgentRepository repository;

    @Override
    protected String doApply(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        log.info("Ai Agent 构建，对话模型节点 {}", JSON.toJSONString(requestParameter));

        List<AiClientVO> aiClientVOList = dynamicContext.getValue("aiClientList");
        Map<Long, AiClientSystemPromptVO> aiClientSystemPromptVOMap = dynamicContext.getValue("aiSystemPromptConfig");
        
        // 获取组装配置（从数据库或配置文件中读取）
        Map<String, Object> assemblyConfig = getAssemblyConfig(requestParameter);

        for (AiClientVO aiClientVO : aiClientVOList) {
            // 使用配置化工厂创建ChatClient，替代硬编码
            ChatClient chatClient = assemblyFactory.createChatClient(
                aiClientVO, 
                aiClientSystemPromptVOMap, 
                assemblyConfig
            );

            registerBean(beanName(aiClientVO.getClientId()), ChatClient.class, chatClient);
        }

        return router(requestParameter, dynamicContext);
    }
    
    /**
     * 获取组装配置
     * 从数据库查询组装配置，替代硬编码
     */
    private Map<String, Object> getAssemblyConfig(AiAgentEngineStarterEntity requestParameter) {
        // 从数据库查询组装配置
        // 这里需要实现具体的查询逻辑
        return repository.queryAssemblyConfigByAgentId(requestParameter.getAgentId());
    }

    @Override
    public StrategyHandler<AiAgentEngineStarterEntity, DefaultArmoryStrategyFactory.DynamicContext, String> get(AiAgentEngineStarterEntity requestParameter, DefaultArmoryStrategyFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }

    @Override
    protected String beanName(Long id) {
        return "ChatClient_" + id;
    }
}
