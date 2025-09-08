# 配置化驱动与数据库集成说明

## 1. 概述
本项目已集成“配置化驱动 + 数据库存储”的能力：通过数据库表维护 Agent、模型、系统提示词、MCP 工具、顾问等配置，运行时查询并装配，避免硬编码。

## 2. 目录与关键位置
- SQL 脚本
  - `docs/dev-ops/mysql/sql/ai-agent-station.sql`（完整初始化）
  - `docs/dev-ops/mysql/sql/ai-agent-station-study.sql`（示例/学习）
- 应用配置（数据源 + MyBatis）
  - `ai-agent-station-app/src/main/resources/application-*.yml`
  - MyBatis：`mybatis.mapper-locations`, `mybatis.config-location`
- 映射文件（Mapper XML）
  - `ai-agent-station-app/src/main/resources/mybatis/mapper/*.xml`
- DAO & PO（数据库访问与实体）
  - `ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/dao/**`
  - `ai-agent-station-infrastructure/src/main/java/cn/bugstack/ai/infrastructure/dao/po/**`

> 说明：若需要“配置化装配（从多表汇聚为可执行 Agent 链）”，源实现位于 master 工程：`infrastructure/adapter/repository/AgentRepository.java` 和 configurable/domain 下的装配服务，可按需复制接入。

## 3. 数据库初始化
1) 启动 MySQL（本地或 Docker，确保端口与账号一致）
2) 创建数据库并导入脚本：
```sql
CREATE DATABASE `ai-agent-station` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```
```bash
# 在 study 工程根目录执行
mysql -u root -p ai-agent-station < docs/dev-ops/mysql/sql/ai-agent-station.sql
```

## 4. 应用配置
在 `application-dev.yml`（或 `test/prod`）中设置数据源：
```yaml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://127.0.0.1:3306/ai-agent-station?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=true
    driver-class-name: com.mysql.cj.jdbc.Driver
mybatis:
  mapper-locations: classpath:/mybatis/mapper/*.xml
  config-location: classpath:/mybatis/config/mybatis-config.xml
```

可选组件（按需开启）：
- PGVector（PostgreSQL 向量库）在 `spring.ai.vectorstore.pgvector.datasource` 下配置
- Redis（已引入 `jedis` 依赖）按需在 `application-*.yml` 增加连接配置

## 5. 运行与测试
- 非数据库单元测试（无需 MySQL）：
  - `ConfigurableUnitTest`：`ai-agent-station-app/src/test/java/ai/agent/ConfigurableUnitTest.java`
  - 运行：`mvn -Dtest=ConfigurableUnitTest test`
- 数据库集成测试（需要 MySQL 与初始化数据）：
  - `ConfigurableTest`：`ai-agent-station-app/src/test/java/ai/agent/ConfigurableTest.java`
  - 运行：`mvn -Dtest=ConfigurableTest test`
- 一键脚本（cmd）：
  - `test-configurable.bat`（包含编译 + 测试 + 报告）

## 6. 组装逻辑（装配思路）
- DAO/Mapper 负责从如下配置表读取数据：
  - 智能体：`ai_agent`、绑定客户端：`ai_agent_client`
  - 模型：`ai_client_model`、绑定：`ai_client_model_config`
  - 工具：`ai_client_tool_mcp`、绑定：`ai_client_tool_config`
  - 系统提示词：`ai_client_system_prompt`、绑定：`ai_client_system_prompt_config`
  - 顾问：`ai_client_advisor`、绑定：`ai_client_advisor_config`
- 装配过程（建议实现）
  1. 根据 Agent 或 Client 列表拉取各维度配置
  2. 组装为领域 VO（如 `AiClientVO`、`AiClientModelVO` 等）
  3. 交由领域装配服务（armory）构建可执行链路（模型 → 工具 → 顾问 → 提示词）

> 参考实现：`AgentRepository#queryAiClientByClientIds`（在 master 工程），把各维度配置映射到 VO，含 `extParam` 的类型化解析（ChatMemory/RagAnswer）。

## 7. 常见问题
- 无法连库：检查 MySQL 启动、端口、账号、密码与 `application-*.yml` 是否一致
- Mapper 报错：确认 `mapper-locations` 路径与 XML 文件存在
- 依赖冲突：如需 Spring AI，请在父 `pom.xml` 导入 BOM 并对齐版本；或暂时注释相关依赖

## 8. 后续扩展
- 接入 master 的装配仓储与 armory 服务，实现全链路“配置化组装”
- 增加更多顾问/工具类型，对 `extParam` 做结构化解析
- 增强管理端，提供在线编辑与生效机制
