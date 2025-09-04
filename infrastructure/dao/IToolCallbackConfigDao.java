package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.ToolCallbackConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 工具回调提供者配置DAO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Mapper
public interface IToolCallbackConfigDao {
    
    /**
     * 根据回调类型查询配置
     */
    ToolCallbackConfig queryToolCallbackConfigByType(@Param("callbackType") String callbackType);
    
    /**
     * 根据ID查询配置
     */
    ToolCallbackConfig queryToolCallbackConfigById(@Param("id") Long id);
    
    /**
     * 查询所有配置
     */
    List<ToolCallbackConfig> queryAllToolCallbackConfigs();
    
    /**
     * 插入配置
     */
    int insert(ToolCallbackConfig config);
    
    /**
     * 更新配置
     */
    int update(ToolCallbackConfig config);
    
    /**
     * 删除配置
     */
    int deleteById(@Param("id") Long id);
}
