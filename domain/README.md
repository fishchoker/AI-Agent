# 领域层配置化改造

## 新增服务类

### 1. 配置化组装工厂 (ConfigurableAssemblyFactory)
替代硬编码的ChatClient组装逻辑，支持动态配置。

### 2. 提示词模板服务 (PromptTemplateService)
管理提示词模板，支持变量替换，替代硬编码的提示词。

### 3. 顾问策略服务 (AdvisorStrategyService)
根据配置动态创建不同类型的Advisor。

### 4. 工具回调服务 (ToolCallbackService)
根据配置创建工具回调提供者。

## 修改的服务类

### 1. AiClientNode
- 原文件：`ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/armory/node/AiClientNode.java`
- 主要改动：使用配置化工厂替代硬编码组装逻辑

### 2. AiAgentChatService
- 原文件：`ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/chat/AiAgentChatService.java`
- 主要改动：使用配置化的RAG提示词模板
