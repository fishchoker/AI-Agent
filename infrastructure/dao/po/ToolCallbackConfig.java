package cn.bugstack.ai.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 工具回调提供者配置PO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
@TableName("ai_tool_callback_config")
public class ToolCallbackConfig {
    
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
    
    /**
     * 状态(0:禁用,1:启用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
