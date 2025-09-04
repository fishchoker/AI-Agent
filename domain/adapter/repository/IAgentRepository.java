package cn.bugstack.ai.domain.agent.adapter.repository;

import cn.bugstack.ai.domain.agent.model.valobj.*;

import java.util.List;
import java.util.Map;

/**
 * Agent数据访问接口 - 配置化改造版本
 * 
 * 原文件：ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/adapter/repository/IAgentRepository.java
 * 主要改动：新增配置化相关的数据访问方法
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
public interface IAgentRepository {

    // 原有方法保持不变
    List<Long> queryAiClientIds();
    List<AiClientVO> queryAiClientByClientIds(List<Long> clientIds);
    List<AiClientModelVO> queryModelConfigByClientIds(List<Long> clientIds);
    List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIds);
    Map<Long, AiClientSystemPromptVO> querySystemPromptConfigByClientIds(List<Long> clientIds);
    List<AiClientToolMcpVO> queryToolMcpConfigByClientIds(List<Long> clientIds);
    Long queryAiClientModelIdByAgentId(Long agentId);
    String queryRagKnowledgeTag(Long ragId);

    // 新增的配置化相关方法
    /**
     * 根据智能体ID查询组装配置
     */
    Map<String, Object> queryAssemblyConfigByAgentId(Long agentId);

    /**
     * 根据模板类型查询提示词模板
     */
    PromptTemplateVO queryPromptTemplateByType(String templateType);

    /**
     * 根据顾问类型查询顾问策略配置
     */
    AdvisorStrategyVO queryAdvisorStrategyByType(String advisorType);

    /**
     * 根据回调类型查询工具回调配置
     */
    ToolCallbackConfigVO queryToolCallbackConfigByType(String callbackType);
}
