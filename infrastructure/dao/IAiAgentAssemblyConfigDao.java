package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiAgentAssemblyConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI智能体组装流程配置DAO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Mapper
public interface IAiAgentAssemblyConfigDao {
    
    /**
     * 根据智能体ID查询组装配置
     */
    List<AiAgentAssemblyConfig> queryAssemblyConfigByAgentId(@Param("agentId") Long agentId);
    
    /**
     * 根据智能体ID和组装类型查询配置
     */
    List<AiAgentAssemblyConfig> queryAssemblyConfigByAgentIdAndType(@Param("agentId") Long agentId, @Param("assemblyType") String assemblyType);
    
    /**
     * 根据ID查询组装配置
     */
    AiAgentAssemblyConfig queryAssemblyConfigById(@Param("id") Long id);
    
    /**
     * 插入组装配置
     */
    int insert(AiAgentAssemblyConfig assemblyConfig);
    
    /**
     * 更新组装配置
     */
    int update(AiAgentAssemblyConfig assemblyConfig);
    
    /**
     * 删除组装配置
     */
    int deleteById(@Param("id") Long id);
}
