# 数据库配置化改造

## 新增表结构

### 1. AI智能体组装流程配置表 (ai_agent_assembly_config)
用于配置AI Agent的组装流程，替代硬编码的组装逻辑。

### 2. 系统提示词模板表 (ai_system_prompt_template)
用于管理各种提示词模板，支持变量替换。

### 3. 顾问创建策略配置表 (ai_advisor_strategy_config)
用于配置不同类型的Advisor创建策略。

### 4. 工具回调提供者配置表 (ai_tool_callback_config)
用于配置工具回调提供者的创建策略。

## 使用说明

1. 执行 `ai-agent-station-configurable.sql` 创建新表
2. 执行 `init-config-data.sql` 插入初始配置数据
3. 根据业务需求调整配置数据
