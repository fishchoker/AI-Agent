package cn.bugstack.ai.domain.agent.model.valobj;

import lombok.Data;

/**
 * 提示词模板值对象
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Data
public class PromptTemplateVO {
    
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
}
