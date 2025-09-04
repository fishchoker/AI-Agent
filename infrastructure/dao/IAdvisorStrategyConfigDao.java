package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AdvisorStrategyConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 顾问创建策略配置DAO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Mapper
public interface IAdvisorStrategyConfigDao {
    
    /**
     * 根据顾问类型查询策略配置
     */
    AdvisorStrategyConfig queryAdvisorStrategyByType(@Param("advisorType") String advisorType);
    
    /**
     * 根据ID查询策略配置
     */
    AdvisorStrategyConfig queryAdvisorStrategyById(@Param("id") Long id);
    
    /**
     * 查询所有策略配置
     */
    List<AdvisorStrategyConfig> queryAllAdvisorStrategies();
    
    /**
     * 插入策略配置
     */
    int insert(AdvisorStrategyConfig strategyConfig);
    
    /**
     * 更新策略配置
     */
    int update(AdvisorStrategyConfig strategyConfig);
    
    /**
     * 删除策略配置
     */
    int deleteById(@Param("id") Long id);
}
