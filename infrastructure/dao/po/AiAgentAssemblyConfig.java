package cn.bugstack.ai.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * AI智能体组装流程配置PO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
@TableName("ai_agent_assembly_config")
public class AiAgentAssemblyConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 智能体ID
     */
    private Long agentId;
    
    /**
     * 组装类型(chat_client/model/advisor/tool)
     */
    private String assemblyType;
    
    /**
     * 组装顺序
     */
    private Integer assemblyOrder;
    
    /**
     * 组装配置JSON
     */
    private String assemblyConfig;
    
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
