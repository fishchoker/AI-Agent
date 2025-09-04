package cn.bugstack.ai.domain.agent.service.template;

import cn.bugstack.ai.domain.agent.model.valobj.PromptTemplateVO;
import cn.bugstack.ai.domain.agent.adapter.repository.IAgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 提示词模板服务
 * 替代硬编码的提示词，支持变量替换
 * 
 * @author Fuzhengwei bugstack.cn @小傅哥
 * 2025-01-XX
 */
@Slf4j
@Service
public class PromptTemplateService {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^}]+)\\}");
    
    @Resource
    private IAgentRepository repository;

    /**
     * 渲染提示词模板
     * 支持{变量名}格式的变量替换
     */
    public String renderTemplate(PromptTemplateVO template, Map<String, Object> variables) {
        if (template == null || template.getTemplateContent() == null) {
            return "AI 智能体";
        }

        String content = template.getTemplateContent();
        
        // 替换模板变量
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();
        
        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = variables.get(variableName);
            String replacement = value != null ? value.toString() : "{" + variableName + "}";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        log.info("提示词模板渲染完成，模板ID: {}, 变量: {}", template.getId(), variables);
        return result.toString();
    }
    
    /**
     * 获取RAG提示词模板
     * 替代AiAgentChatService中硬编码的RAG提示词
     */
    public String getRagPromptTemplate(Map<String, Object> variables) {
        // 从数据库查询RAG提示词模板
        PromptTemplateVO template = repository.queryPromptTemplateByType("rag");
        if (template != null) {
            return renderTemplate(template, variables);
        }
        
        // 如果数据库中没有配置，使用默认模板
        String defaultTemplate = """
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            Another thing you need to note is that your reply must be in Chinese!
            DOCUMENTS:
                {documents}
            """;
        
        PromptTemplateVO defaultTemplateVO = new PromptTemplateVO();
        defaultTemplateVO.setTemplateContent(defaultTemplate);
        return renderTemplate(defaultTemplateVO, variables);
    }
    
    /**
     * 获取默认系统提示词模板
     */
    public String getDefaultPromptTemplate(Map<String, Object> variables) {
        // 从数据库查询默认提示词模板
        PromptTemplateVO template = repository.queryPromptTemplateByType("default");
        if (template != null) {
            return renderTemplate(template, variables);
        }
        
        // 如果数据库中没有配置，使用默认模板
        PromptTemplateVO defaultTemplateVO = new PromptTemplateVO();
        defaultTemplateVO.setTemplateContent("你是一个AI智能体，可以帮助用户解决问题。");
        return renderTemplate(defaultTemplateVO, variables);
    }
}
