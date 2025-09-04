# MyBatis映射文件配置化改造

## 新增映射文件

### 1. 组装配置映射
- `ai_agent_assembly_config_mapper.xml` - AI智能体组装流程配置映射

### 2. 提示词模板映射
- `ai_system_prompt_template_mapper.xml` - 系统提示词模板映射

### 3. 顾问策略映射
- `ai_advisor_strategy_config_mapper.xml` - 顾问创建策略配置映射

### 4. 工具回调映射
- `ai_tool_callback_config_mapper.xml` - 工具回调提供者配置映射

## 修改的映射文件

### 1. 智能体映射
- 原文件：`ai-agent-station-app/src/main/resources/mybatis/mapper/ai_agent_mapper.xml`
- 主要改动：新增组装配置查询方法
