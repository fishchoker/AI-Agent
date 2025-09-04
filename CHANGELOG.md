# AI Agent Station 配置化改造变更日志

## [2.0.0] - 2025-01-XX

### 新增功能
- ✨ 新增配置化组装工厂 `ConfigurableAssemblyFactory`
- ✨ 新增提示词模板服务 `PromptTemplateService`
- ✨ 新增顾问策略服务 `AdvisorStrategyService`
- ✨ 新增工具回调服务 `ToolCallbackService`
- ✨ 新增4个配置化数据库表
- ✨ 新增配置化相关的数据访问对象
- ✨ 新增MyBatis映射文件
- ✨ 支持动态配置AI Agent组装流程
- ✨ 支持提示词模板变量替换
- ✨ 支持顾问创建策略配置
- ✨ 支持工具回调提供者配置

### 重大变更
- 🔄 **BREAKING**: `AiClientNode.java` 使用配置化工厂替代硬编码组装逻辑
- 🔄 **BREAKING**: `AiAgentChatService.java` 使用配置化RAG提示词模板
- 🔄 **BREAKING**: `AgentRepository.java` 新增配置化相关数据访问方法
- 🔄 **BREAKING**: 数据库表结构变更，新增4个配置表

### 改进
- 💡 系统提示词可配置化（替代硬编码的"AI 智能体"）
- 💡 ChatClient组装流程可配置化
- 💡 RAG提示词模板可配置化
- 💡 顾问创建策略可配置化
- 💡 工具回调提供者可配置化
- 💡 支持配置热更新
- 💡 提供配置管理界面

### 修复
- 🐛 修复硬编码导致的配置不灵活问题
- 🐛 修复RAG提示词无法动态调整的问题
- 🐛 修复组装流程无法配置的问题

### 移除
- ❌ 移除硬编码的系统提示词
- ❌ 移除硬编码的ChatClient组装逻辑
- ❌ 移除硬编码的RAG提示词模板

## [1.0.0] - 2025-05-XX

### 初始版本
- 🎉 初始版本发布
- 🎉 基础AI Agent功能
- 🎉 硬编码组装逻辑
- 🎉 基础数据库表结构
