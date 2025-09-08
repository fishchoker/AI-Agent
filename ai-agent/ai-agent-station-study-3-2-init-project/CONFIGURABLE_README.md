# AI Agent Station 配置化功能说明

## 概述

本项目已成功集成了ai-agent-station-master中的配置化驱动和数据库相关功能，实现了通过数据库配置来驱动AI智能体的行为。

## 主要功能

### 1. 数据库配置化
- **智能体配置表 (ai_agent)**: 管理AI智能体的基本信息
- **客户端配置表 (ai_client)**: 管理AI客户端配置
- **模型配置表 (ai_client_model)**: 管理AI模型配置
- **工具配置表 (ai_client_tool_mcp)**: 管理MCP工具配置
- **系统提示词配置表 (ai_client_system_prompt)**: 管理系统提示词
- **顾问配置表 (ai_client_advisor)**: 管理AI顾问配置

### 2. 配置化驱动
- 通过数据库配置动态加载智能体
- 支持多种渠道类型 (agent, chat_stream)
- 支持任务调度配置
- 支持模型和工具的动态配置

## 数据库初始化

### 1. 创建数据库
```sql
CREATE DATABASE `ai-agent-station` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

### 2. 执行初始化脚本
```bash
mysql -u root -p ai-agent-station < docs/dev-ops/mysql/sql/ai-agent-station.sql
```

## 配置文件说明

### application-dev.yml
- 开发环境数据库配置
- 包含Spring AI配置
- 包含向量数据库(PGVector)配置

### application-prod.yml
- 生产环境数据库配置
- 优化的连接池配置

### application-test.yml
- 测试环境数据库配置
- 简化的配置用于测试

## 主要依赖

### Spring AI
- spring-ai-openai-spring-boot-starter
- spring-ai-pgvector-store-spring-boot-starter
- spring-ai-vectorstore-pgvector

### 数据库
- MySQL 8.0+ (主数据库)
- PostgreSQL (向量数据库)
- Redis (缓存)

### 其他
- MyBatis (ORM框架)
- Jedis (Redis客户端)

## 使用示例

### 1. 查询所有智能体配置
```java
@Resource
private IAiAgentDao aiAgentDao;

List<AiAgent> agentList = aiAgentDao.queryAllAgentConfig();
```

### 2. 查询有效客户端ID
```java
List<Long> clientIds = aiAgentDao.queryValidClientIds();
```

### 3. 查询模型配置
```java
@Resource
private IAiClientModelDao aiClientModelDao;

List<AiClientModel> modelList = aiClientModelDao.queryAllModelConfig();
```

## 测试

运行配置化功能测试：
```bash
mvn test -Dtest=ConfigurableTest
```

## 注意事项

1. 确保MySQL数据库已启动并创建了相应的数据库
2. 确保PostgreSQL数据库已启动并创建了向量数据库
3. 确保Redis服务已启动
4. 根据实际环境修改配置文件中的数据库连接信息
5. 确保所有依赖都已正确安装

## 扩展功能

可以通过以下方式扩展配置化功能：

1. 添加新的配置表
2. 创建对应的DAO接口和PO实体类
3. 添加MyBatis映射文件
4. 在业务逻辑中使用配置化数据

## 故障排除

1. 数据库连接失败：检查数据库服务是否启动，连接信息是否正确
2. 依赖冲突：检查Maven依赖版本是否兼容
3. 配置错误：检查application.yml配置文件格式是否正确
