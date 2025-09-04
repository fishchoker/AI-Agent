# AI Agent Station 配置化改造使用说明

## 快速开始

### 1. 数据库准备
```bash
# 1. 创建数据库表
mysql -u root -p ai-agent-station < database/sql/ai-agent-station-configurable.sql

# 2. 插入初始配置数据
mysql -u root -p ai-agent-station < database/sql/init-config-data.sql
```

### 2. 代码部署
```bash
# 1. 备份原项目
cp -r ai-agent-station ai-agent-station-backup

# 2. 复制新文件到原项目
cp -r ai-agent-station-configurable/domain/* ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/
cp -r ai-agent-station-configurable/infrastructure/* ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/
cp -r ai-agent-station-configurable/mapper/* ai-agent-station-app/src/main/resources/mybatis/mapper/

# 3. 替换被替代的文件
cp ai-agent-station-configurable/domain/service/armory/node/AiClientNode.java ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/armory/node/
cp ai-agent-station-configurable/domain/service/chat/AiAgentChatService.java ai-agent-station-domain/src/main/java/cn/bugstack/ai/domain/agent/service/chat/
cp ai-agent-station-configurable/infrastructure/dao/AgentRepository.java ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/adapter/repository/
```

### 3. 运行测试
```bash
# Windows
ai-agent-station-configurable/run-test.bat

# Linux/Mac
ai-agent-station-configurable/run-test.sh
```

## 配置管理

### 1. 组装流程配置
通过 `ai_agent_assembly_config` 表配置AI Agent的组装流程：

```sql
-- 配置智能体1的组装流程
INSERT INTO ai_agent_assembly_config (agent_id, assembly_type, assembly_order, assembly_config, status) 
VALUES (1, 'chat_client', 1, '{
    "enableDefaultSystem": true,
    "enableToolCallbacks": true,
    "enableAdvisors": true,
    "defaultSystemPrompt": "你是一个专业的AI助手，可以帮助用户解决各种问题。"
}', 1);
```

### 2. 提示词模板配置
通过 `ai_system_prompt_template` 表管理提示词模板：

```sql
-- 添加自定义提示词模板
INSERT INTO ai_system_prompt_template (template_name, template_type, template_content, description, status) 
VALUES ('技术专家', 'custom', '你是一个{expertise}专家，擅长{skills}。今天是{current_date}。', '技术专家模板', 1);
```

### 3. 顾问策略配置
通过 `ai_advisor_strategy_config` 表配置顾问创建策略：

```sql
-- 添加自定义顾问策略
INSERT INTO ai_advisor_strategy_config (advisor_type, strategy_class, strategy_config, description, status) 
VALUES ('CustomAdvisor', 'com.example.CustomAdvisor', '{"param1": "value1", "param2": "value2"}', '自定义顾问', 1);
```

### 4. 工具回调配置
通过 `ai_tool_callback_config` 表配置工具回调提供者：

```sql
-- 添加自定义工具回调
INSERT INTO ai_tool_callback_config (callback_type, provider_class, provider_config, description, status) 
VALUES ('custom', 'com.example.CustomToolCallbackProvider', '{"timeout": 30000}', '自定义工具回调', 1);
```

## 功能验证

### 1. 验证配置化组装
```java
// 测试配置化组装工厂
@Resource
private ConfigurableAssemblyFactory assemblyFactory;

@Test
public void testAssembly() {
    // 创建测试数据并验证组装结果
    ChatClient chatClient = assemblyFactory.createChatClient(clientVO, systemPromptMap, assemblyConfig);
    assertNotNull(chatClient);
}
```

### 2. 验证提示词模板
```java
// 测试提示词模板服务
@Resource
private PromptTemplateService promptTemplateService;

@Test
public void testPromptTemplate() {
    Map<String, Object> variables = Map.of("documents", "测试文档");
    String prompt = promptTemplateService.getRagPromptTemplate(variables);
    assertTrue(prompt.contains("测试文档"));
}
```

### 3. 验证顾问策略
```java
// 测试顾问策略服务
@Resource
private AdvisorStrategyService advisorStrategyService;

@Test
public void testAdvisorStrategy() {
    Advisor advisor = advisorStrategyService.createAdvisorByType("PromptChatMemory", config);
    assertNotNull(advisor);
}
```

## 故障排查

### 1. 编译错误
- 检查所有导入语句是否正确
- 确保所有依赖的类都已创建
- 验证包名和类名是否正确

### 2. 运行时错误
- 检查数据库连接配置
- 验证数据库表是否已创建
- 查看应用日志中的错误信息

### 3. 配置不生效
- 检查数据库中的配置数据
- 验证配置JSON格式是否正确
- 确认配置状态为启用(1)

## 性能优化

### 1. 配置缓存
- 启用配置缓存机制
- 设置合理的缓存过期时间
- 监控缓存命中率

### 2. 数据库优化
- 为配置表添加合适的索引
- 定期清理无效配置
- 优化查询语句

### 3. 内存管理
- 监控内存使用情况
- 及时释放不需要的对象
- 优化对象创建和销毁

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
