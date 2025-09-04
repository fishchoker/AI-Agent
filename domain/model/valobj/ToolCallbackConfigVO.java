package cn.bugstack.ai.domain.agent.model.valobj;

import lombok.Data;

/**
 * 工具回调配置值对象
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
public class ToolCallbackConfigVO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 回调类型(mcp/function_call)
     */
    private String callbackType;
    
    /**
     * 提供者实现类
     */
    private String providerClass;
    
    /**
     * 提供者配置JSON
     */
    private String providerConfig;
    
    /**
     * 描述
     */
    private String description;
}
