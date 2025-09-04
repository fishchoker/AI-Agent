package cn.bugstack.ai.infrastructure.dao;

import cn.bugstack.ai.infrastructure.dao.po.AiSystemPromptTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统提示词模板DAO
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Mapper
public interface IAiSystemPromptTemplateDao {
    
    /**
     * 根据模板类型查询提示词模板
     */
    AiSystemPromptTemplate queryPromptTemplateByType(@Param("templateType") String templateType);
    
    /**
     * 根据模板名称查询提示词模板
     */
    AiSystemPromptTemplate queryPromptTemplateByName(@Param("templateName") String templateName);
    
    /**
     * 根据ID查询提示词模板
     */
    AiSystemPromptTemplate queryPromptTemplateById(@Param("id") Long id);
    
    /**
     * 查询所有提示词模板
     */
    List<AiSystemPromptTemplate> queryAllPromptTemplates();
    
    /**
     * 插入提示词模板
     */
    int insert(AiSystemPromptTemplate template);
    
    /**
     * 更新提示词模板
     */
    int update(AiSystemPromptTemplate template);
    
    /**
     * 删除提示词模板
     */
    int deleteById(@Param("id") Long id);
}
