-- AI Agent Station 配置化改造 - 初始配置数据
-- 创建时间: 2025-01-XX
-- 说明: 插入配置化改造所需的初始数据

-- 1. 插入默认的提示词模板
INSERT INTO `ai_system_prompt_template` (`template_name`, `template_type`, `template_content`, `description`, `status`) VALUES
('默认智能体', 'default', '你是一个AI智能体，可以帮助用户解决问题。', '默认的系统提示词', 1),
('RAG知识问答', 'rag', 'Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.\nIf unsure, simply state that you don''t know.\nAnother thing you need to note is that your reply must be in Chinese!\nDOCUMENTS:\n{documents}', 'RAG知识库问答模板', 1),
('专业助手', 'custom', '你是一个专业的AI助手，擅长{expertise}领域。今天是{current_date}。', '自定义专业助手模板', 1);

-- 2. 插入顾问策略配置
INSERT INTO `ai_advisor_strategy_config` (`advisor_type`, `strategy_class`, `strategy_config`, `description`, `status`) VALUES
('PromptChatMemory', 'org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor', '{"maxMessages": 200}', '提示词记忆顾问', 1),
('RagAnswer', 'org.springframework.ai.chat.client.advisor.RagAnswerAdvisor', '{"topK": 5, "filterExpression": "knowledge == ''知识库名称''"}', 'RAG答案顾问', 1),
('SimpleLogger', 'org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor', '{}', '简单日志顾问', 1);

-- 3. 插入工具回调配置
INSERT INTO `ai_tool_callback_config` (`callback_type`, `provider_class`, `provider_config`, `description`, `status`) VALUES
('mcp', 'org.springframework.ai.mcp.SyncMcpToolCallbackProvider', '{}', 'MCP工具回调提供者', 1),
('function_call', 'org.springframework.ai.tool.FunctionCallbackProvider', '{}', 'Function Call工具回调提供者', 1);

-- 4. 插入组装流程配置示例
INSERT INTO `ai_agent_assembly_config` (`agent_id`, `assembly_type`, `assembly_order`, `assembly_config`, `status`) VALUES
(1, 'chat_client', 1, '{"enableDefaultSystem": true, "enableToolCallbacks": true, "enableAdvisors": true, "defaultSystemPrompt": "你是一个专业的AI助手，可以帮助用户解决各种问题。"}', 1),
(2, 'chat_client', 1, '{"enableDefaultSystem": true, "enableToolCallbacks": true, "enableAdvisors": true, "defaultSystemPrompt": "你是一个自动发帖和通知的AI助手。"}', 1),
(3, 'chat_client', 1, '{"enableDefaultSystem": true, "enableToolCallbacks": true, "enableAdvisors": false, "defaultSystemPrompt": "你是一个文件操作助手。"}', 1),
(4, 'chat_client', 1, '{"enableDefaultSystem": true, "enableToolCallbacks": true, "enableAdvisors": true, "defaultSystemPrompt": "你是一个智能对话助手。"}', 1);
