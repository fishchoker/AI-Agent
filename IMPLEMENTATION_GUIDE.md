# AI Agent Station 配置化改造实施指南

## 概述
本指南详细说明如何将AI Agent Station项目从硬编码改造为配置化架构。

## 改造目标
- 用数据库表替代代码中的硬编码流程
- 支持动态配置AI Agent的组装逻辑
- 提供灵活的提示词模板管理
- 实现顾问和工具的配置化管理

## 实施步骤

### 第一步：数据库准备
1. 执行 `database/sql/ai-agent-station-configurable.sql` 创建新表
2. 执行 `database/sql/init-config-data.sql` 插入初始配置数据
3. 验证数据库表结构正确

### 第二步：代码替换
1. 备份原项目文件
2. 替换以下文件：
   - `domain/service/armory/node/AiClientNode.java` → 原文件位置
   - `domain/service/chat/AiAgentChatService.java` → 原文件位置
   - `infrastructure/dao/AgentRepository.java` → 原文件位置

3. 新增以下文件到对应目录：
   - 配置化服务类到 `domain/service/` 目录
   - 数据访问对象到 `infrastructure/dao/` 目录
   - MyBatis映射文件到 `resources/mybatis/mapper/` 目录

### 第三步：配置更新
1. 更新 `application.yml` 配置文件
2. 添加配置化相关配置项
3. 更新MyBatis映射文件路径

### 第四步：测试验证
1. 启动应用验证配置加载
2. 测试AI Agent组装功能
3. 验证提示词模板功能
4. 测试配置热更新功能

## 配置管理

### 1. 组装流程配置
通过 `ai_agent_assembly_config` 表配置AI Agent的组装流程：
```sql
INSERT INTO ai_agent_assembly_config (agent_id, assembly_type, assembly_order, assembly_config, status) 
VALUES (1, 'chat_client', 1, '{"enableDefaultSystem": true, "enableToolCallbacks": true, "enableAdvisors": true}', 1);
```

### 2. 提示词模板配置
通过 `ai_system_prompt_template` 表管理提示词模板：
```sql
INSERT INTO ai_system_prompt_template (template_name, template_type, template_content, description, status) 
VALUES ('自定义助手', 'custom', '你是一个{role}，擅长{expertise}。', '自定义角色助手模板', 1);
```

### 3. 顾问策略配置
通过 `ai_advisor_strategy_config` 表配置顾问创建策略：
```sql
INSERT INTO ai_advisor_strategy_config (advisor_type, strategy_class, strategy_config, description, status) 
VALUES ('CustomAdvisor', 'com.example.CustomAdvisor', '{"param1": "value1"}', '自定义顾问', 1);
```

### 4. 工具回调配置
通过 `ai_tool_callback_config` 表配置工具回调提供者：
```sql
INSERT INTO ai_tool_callback_config (callback_type, provider_class, provider_config, description, status) 
VALUES ('custom', 'com.example.CustomToolCallbackProvider', '{}', '自定义工具回调', 1);
```

## 扩展开发

### 1. 新增提示词模板类型
1. 在 `ai_system_prompt_template` 表中添加新记录
2. 在 `PromptTemplateService` 中添加对应的处理逻辑
3. 更新配置化工厂的模板选择逻辑

### 2. 新增顾问类型
1. 在 `ai_advisor_strategy_config` 表中添加新记录
2. 实现对应的Advisor类
3. 在 `AdvisorStrategyService` 中添加创建逻辑

### 3. 新增工具回调类型
1. 在 `ai_tool_callback_config` 表中添加新记录
2. 实现对应的ToolCallbackProvider类
3. 在 `ToolCallbackService` 中添加创建逻辑

## 注意事项

### 1. 兼容性
- 保持原有API接口不变
- 支持渐进式迁移
- 提供配置回退机制

### 2. 性能考虑
- 配置数据缓存机制
- 避免频繁数据库查询
- 合理设置缓存过期时间

### 3. 安全性
- 配置数据验证
- 防止SQL注入
- 敏感信息加密存储

## 故障排查

### 1. 配置加载失败
- 检查数据库连接
- 验证表结构是否正确
- 查看应用日志错误信息

### 2. 组装逻辑异常
- 检查组装配置JSON格式
- 验证依赖的Bean是否存在
- 查看配置化工厂日志

### 3. 提示词模板错误
- 检查模板变量格式
- 验证变量替换逻辑
- 查看模板服务日志

## 监控和维护

### 1. 配置监控
- 监控配置加载时间
- 统计配置使用频率
- 记录配置变更历史

### 2. 性能监控
- 监控组装耗时
- 统计模板渲染时间
- 记录异常情况

### 3. 定期维护
- 清理无效配置
- 优化配置结构
- 更新配置文档
