# 被替代的原文件列表

## 完全替代的文件

### 1. 领域层服务类
**原文件：** `ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/armory/node/AiClientNode.java`
**替代文件：** `domain/service/armory/node/AiClientNode.java`
**替代原因：** 使用配置化工厂替代硬编码的ChatClient组装逻辑

**原文件：** `ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/chat/AiAgentChatService.java`
**替代文件：** `domain/service/chat/AiAgentChatService.java`
**替代原因：** 使用配置化的RAG提示词模板替代硬编码

### 2. 基础设施层
**原文件：** `ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/adapter/repository/AgentRepository.java`
**替代文件：** `infrastructure/dao/AgentRepository.java`
**替代原因：** 新增配置化相关的数据访问方法

### 3. MyBatis映射文件
**原文件：** `ai-agent-station-app/src/main/resources/mybatis/mapper/ai_agent_mapper.xml`
**替代文件：** `mapper/ai_agent_mapper.xml`
**替代原因：** 新增组装配置查询方法

## 新增的文件

### 1. 配置化服务类
- `domain/service/armory/ConfigurableAssemblyFactory.java` - 配置化组装工厂
- `domain/service/template/PromptTemplateService.java` - 提示词模板服务
- `domain/service/strategy/AdvisorStrategyService.java` - 顾问策略服务
- `domain/service/callback/ToolCallbackService.java` - 工具回调服务

### 2. 数据访问对象
- `infrastructure/dao/po/AiAgentAssemblyConfig.java` - 组装配置PO
- `infrastructure/dao/po/AiSystemPromptTemplate.java` - 提示词模板PO
- `infrastructure/dao/po/AdvisorStrategyConfig.java` - 顾问策略PO
- `infrastructure/dao/po/ToolCallbackConfig.java` - 工具回调PO
- `infrastructure/dao/IAiAgentAssemblyConfigDao.java` - 组装配置DAO
- `infrastructure/dao/IAiSystemPromptTemplateDao.java` - 提示词模板DAO
- `infrastructure/dao/IAdvisorStrategyConfigDao.java` - 顾问策略DAO
- `infrastructure/dao/IToolCallbackConfigDao.java` - 工具回调DAO

### 3. MyBatis映射文件
- `mapper/ai_agent_assembly_config_mapper.xml` - 组装配置映射
- `mapper/ai_system_prompt_template_mapper.xml` - 提示词模板映射
- `mapper/ai_advisor_strategy_config_mapper.xml` - 顾问策略映射
- `mapper/ai_tool_callback_config_mapper.xml` - 工具回调映射

### 4. 数据库脚本
- `database/sql/ai-agent-station-configurable.sql` - 新增表结构
- `database/sql/init-config-data.sql` - 初始配置数据

## 修改说明

### 主要改动点：
1. **AiClientNode.java** - 第34-79行的硬编码组装逻辑被配置化工厂替代
2. **AiAgentChatService.java** - 第102-108行的硬编码RAG提示词被模板服务替代
3. **AgentRepository.java** - 新增配置化相关的查询方法
4. **数据库表** - 新增4个配置表，支持动态配置管理

### 兼容性说明：
- 保持原有API接口不变
- 向后兼容现有配置
- 支持渐进式迁移
