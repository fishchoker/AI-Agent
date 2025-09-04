# 基础设施层配置化改造

## 新增数据访问对象

### 1. 持久化对象 (PO)
- `AiAgentAssemblyConfig.java` - 组装配置PO
- `AiSystemPromptTemplate.java` - 提示词模板PO
- `AdvisorStrategyConfig.java` - 顾问策略PO
- `ToolCallbackConfig.java` - 工具回调PO

### 2. 数据访问接口 (DAO)
- `IAiAgentAssemblyConfigDao.java` - 组装配置DAO
- `IAiSystemPromptTemplateDao.java` - 提示词模板DAO
- `IAdvisorStrategyConfigDao.java` - 顾问策略DAO
- `IToolCallbackConfigDao.java` - 工具回调DAO

## 修改的文件

### 1. AgentRepository
- 原文件：`ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/adapter/repository/AgentRepository.java`
- 主要改动：新增配置化相关的数据访问方法
