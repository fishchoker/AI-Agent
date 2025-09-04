package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import cn.bugstack.ai.domain.agent.model.valobj.*;
import cn.bugstack.ai.infrastructure.dao.po.*;
import org.springframework.stereotype.Repository;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agent数据访问实现 - 配置化改造版本
 * 
 * 原文件：ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/adapter/repository/AgentRepository.java
 * 主要改动：新增配置化相关的数据访问方法
 *
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Repository
public class AgentRepository implements IAgentRepository {

    @Resource
    private IAiAgentDao aiAgentDao;
    @Resource
    private IAiClientDao aiClientDao;
    @Resource
    private IAiClientModelDao aiClientModelDao;
    @Resource
    private IAiClientAdvisorDao aiClientAdvisorDao;
    @Resource
    private IAiClientSystemPromptDao aiClientSystemPromptDao;
    @Resource
    private IAiClientToolMcpDao aiClientToolMcpDao;
    @Resource
    private IAiRagOrderDao aiRagOrderDao;
    
    // 新增的配置化相关DAO
    @Resource
    private IAiAgentAssemblyConfigDao aiAgentAssemblyConfigDao;
    @Resource
    private IAiSystemPromptTemplateDao aiSystemPromptTemplateDao;
    @Resource
    private IAdvisorStrategyConfigDao advisorStrategyConfigDao;
    @Resource
    private IToolCallbackConfigDao toolCallbackConfigDao;

    // 原有方法保持不变...
    @Override
    public List<Long> queryAiClientIds() {
        return aiClientDao.queryAiClientIds();
    }

    @Override
    public List<AiClientVO> queryAiClientByClientIds(List<Long> clientIds) {
        return aiClientDao.queryAiClientByClientIds(clientIds);
    }

    @Override
    public List<AiClientModelVO> queryModelConfigByClientIds(List<Long> clientIds) {
        return aiClientModelDao.queryModelConfigByClientIds(clientIds);
    }

    @Override
    public List<AiClientAdvisorVO> queryAdvisorConfigByClientIds(List<Long> clientIds) {
        return aiClientAdvisorDao.queryAdvisorConfigByClientIds(clientIds);
    }

    @Override
    public Map<Long, AiClientSystemPromptVO> querySystemPromptConfigByClientIds(List<Long> clientIds) {
        return aiClientSystemPromptDao.querySystemPromptConfigByClientIds(clientIds);
    }

    @Override
    public List<AiClientToolMcpVO> queryToolMcpConfigByClientIds(List<Long> clientIds) {
        return aiClientToolMcpDao.queryToolMcpConfigByClientIds(clientIds);
    }

    @Override
    public Long queryAiClientModelIdByAgentId(Long agentId) {
        return aiClientModelDao.queryAiClientModelIdByAgentId(agentId);
    }

    @Override
    public String queryRagKnowledgeTag(Long ragId) {
        return aiRagOrderDao.queryRagKnowledgeTag(ragId);
    }

    // 新增的配置化相关方法
    @Override
    public Map<String, Object> queryAssemblyConfigByAgentId(Long agentId) {
        // 查询组装配置并转换为Map
        List<AiAgentAssemblyConfig> configs = aiAgentAssemblyConfigDao.queryAssemblyConfigByAgentId(agentId);
        
        Map<String, Object> assemblyConfig = new HashMap<>();
        
        // 设置默认配置
        assemblyConfig.put("enableDefaultSystem", true);
        assemblyConfig.put("enableToolCallbacks", true);
        assemblyConfig.put("enableAdvisors", true);
        assemblyConfig.put("defaultSystemPrompt", "AI 智能体");
        
        // 如果有配置数据，则使用配置数据覆盖默认值
        if (configs != null && !configs.isEmpty()) {
            for (AiAgentAssemblyConfig config : configs) {
                if ("chat_client".equals(config.getAssemblyType())) {
                    // 解析JSON配置
                    if (config.getAssemblyConfig() != null && !config.getAssemblyConfig().isEmpty()) {
                        try {
                            // 这里可以使用JSON解析库来解析配置
                            // 暂时使用简单的字符串处理
                            String configStr = config.getAssemblyConfig();
                            if (configStr.contains("enableDefaultSystem")) {
                                assemblyConfig.put("enableDefaultSystem", configStr.contains("\"enableDefaultSystem\": true"));
                            }
                            if (configStr.contains("enableToolCallbacks")) {
                                assemblyConfig.put("enableToolCallbacks", configStr.contains("\"enableToolCallbacks\": true"));
                            }
                            if (configStr.contains("enableAdvisors")) {
                                assemblyConfig.put("enableAdvisors", configStr.contains("\"enableAdvisors\": true"));
                            }
                            if (configStr.contains("defaultSystemPrompt")) {
                                // 提取defaultSystemPrompt的值
                                int start = configStr.indexOf("\"defaultSystemPrompt\": \"") + 24;
                                int end = configStr.indexOf("\"", start);
                                if (start > 23 && end > start) {
                                    String prompt = configStr.substring(start, end);
                                    assemblyConfig.put("defaultSystemPrompt", prompt);
                                }
                            }
                        } catch (Exception e) {
                            // 解析失败时使用默认配置
                        }
                    }
                }
            }
        }
        
        return assemblyConfig;
    }

    @Override
    public PromptTemplateVO queryPromptTemplateByType(String templateType) {
        AiSystemPromptTemplate template = aiSystemPromptTemplateDao.queryPromptTemplateByType(templateType);
        // 转换为VO对象
        return convertToPromptTemplateVO(template);
    }

    @Override
    public AdvisorStrategyVO queryAdvisorStrategyByType(String advisorType) {
        AdvisorStrategyConfig config = advisorStrategyConfigDao.queryAdvisorStrategyByType(advisorType);
        // 转换为VO对象
        return convertToAdvisorStrategyVO(config);
    }

    @Override
    public ToolCallbackConfigVO queryToolCallbackConfigByType(String callbackType) {
        ToolCallbackConfig config = toolCallbackConfigDao.queryToolCallbackConfigByType(callbackType);
        // 转换为VO对象
        return convertToToolCallbackConfigVO(config);
    }

    // 转换方法
    private PromptTemplateVO convertToPromptTemplateVO(AiSystemPromptTemplate template) {
        if (template == null) {
            return null;
        }
        PromptTemplateVO vo = new PromptTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateType(template.getTemplateType());
        vo.setTemplateContent(template.getTemplateContent());
        vo.setTemplateVariables(template.getTemplateVariables());
        vo.setDescription(template.getDescription());
        return vo;
    }

    private AdvisorStrategyVO convertToAdvisorStrategyVO(AdvisorStrategyConfig config) {
        if (config == null) {
            return null;
        }
        AdvisorStrategyVO vo = new AdvisorStrategyVO();
        vo.setId(config.getId());
        vo.setAdvisorType(config.getAdvisorType());
        vo.setStrategyClass(config.getStrategyClass());
        vo.setStrategyConfig(config.getStrategyConfig());
        vo.setDescription(config.getDescription());
        return vo;
    }

    private ToolCallbackConfigVO convertToToolCallbackConfigVO(ToolCallbackConfig config) {
        if (config == null) {
            return null;
        }
        ToolCallbackConfigVO vo = new ToolCallbackConfigVO();
        vo.setId(config.getId());
        vo.setCallbackType(config.getCallbackType());
        vo.setProviderClass(config.getProviderClass());
        vo.setProviderConfig(config.getProviderConfig());
        vo.setDescription(config.getDescription());
        return vo;
    }
}
