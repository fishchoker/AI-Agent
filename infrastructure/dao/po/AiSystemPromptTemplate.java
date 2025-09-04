package cn.bugstack.ai.infrastructure.dao.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统提示词模板PO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
@TableName("ai_system_prompt_template")
public class AiSystemPromptTemplate {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 模板类型(default/rag/custom)
     */
    private String templateType;
    
    /**
     * 模板内容
     */
    private String templateContent;
    
    /**
     * 模板变量配置JSON
     */
    private String templateVariables;
    
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
