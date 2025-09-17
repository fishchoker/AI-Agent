package cn.bugstack.ai.domain.agent.model.valobj.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/27 17:25
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum AiClientTypeEnumVO {

    DEFAULT("DEFAULT", "通用的"),
    TASK_ANALYZER_CLIENT("TASK_ANALYZER_CLIENT", "任务分析和状态判断"),
    PRECISION_EXECUTOR_CLIENT("PRECISION_EXECUTOR_CLIENT", "具体任务执行"),
    QUALITY_SUPERVISOR_CLIENT("QUALITY_SUPERVISOR_CLIENT", "质量检查和优化"),
    RESPONSE_ASSISTANT("RESPONSE_ASSISTANT", "智能响应助手"),

    TOOL_MCP_CLIENT("TOOL_MCP_CLIENT", "工具分析"),
    PLANNING_CLIENT("PLANNING_CLIENT","任务规划"),
    EXECUTOR_CLIENT("EXECUTOR_CLIENT", "任务执行"),
    
    KNOWLEDGE_RETRIEVAL_CLIENT("KNOWLEDGE_RETRIEVAL_CLIENT", "知识库检索执行"),
    WEB_SEARCH_CLIENT("WEB_SEARCH_CLIENT", "网页搜索执行"),
    INTELLIGENT_RESPONSE_CLIENT("INTELLIGENT_RESPONSE_CLIENT", "结果整合与智能回答"),
    INTENT_ANALYZER_CLIENT("INTENT_ANALYZER_CLIENT", "意图识别"),
    TOOL_ANALYZER_CLIENT("TOOL_ANALYZER_CLIENT", "工具能力分析"),
    TOOL_EXECUTOR_CLIENT("TOOL_EXECUTOR_CLIENT", "工具调用"),
    RESPONSE_BUILDER_CLIENT("RESPONSE_BUILDER_CLIENT", "响应构建")

    ;

    private String code;
    private String info;

}
