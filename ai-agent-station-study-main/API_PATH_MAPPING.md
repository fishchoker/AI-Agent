# AI Agent Station Study - API 路径映射文档

## 项目信息
- **项目名称**: AI Agent Station Study
- **版本**: 1.0-SNAPSHOT
- **基础URL**: `http://localhost:8091`
- **更新时间**: 2025-09-15

## 接口分类

### 1. 业务接口 (Business APIs)

#### 1.1 RAG相关接口
**基础路径**: `/rag/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/rag/analyze_git_repository` | 解析Git仓库 | repoUrl, userName, token | Map<String, Object> |

**请求示例**:
```bash
POST /rag/analyze_git_repository
Content-Type: application/x-www-form-urlencoded

repoUrl=https://github.com/example/repo&userName=username&token=token
```

**响应示例**:
```json
{
  "code": "0000",
  "info": "仓库解析成功",
  "data": {
    "repoId": "12345",
    "fileCount": 150,
    "branchCount": 3
  }
}
```

### 2. 管理接口 (Admin APIs)

#### 2.1 AI代理管理
**基础路径**: `/ai/admin/agent/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/agent/queryAiAgentList` | 查询AI代理列表 | AiAgent | List<AiAgent> |
| POST | `/ai/admin/agent/queryAllAgentConfigListByChannel` | 按渠道查询代理配置 | Map<String, Object> | List<AiAgent> |
| POST | `/ai/admin/agent/queryAiAgentDetail` | 查询AI代理详情 | AiAgent | AiAgent |
| GET | `/ai/admin/agent/queryAiAgentDetail` | 查询AI代理详情 | id | AiAgent |
| GET | `/ai/admin/agent/queryAiAgentById` | 根据ID查询AI代理详情 | id | AiAgent |
| POST | `/ai/admin/agent/addAiAgent` | 新增AI代理 | AiAgent | Boolean |
| POST | `/ai/admin/agent/updateAiAgent` | 更新AI代理 | AiAgent | Boolean |
| POST | `/ai/admin/agent/deleteAiAgent` | 删除AI代理 | AiAgent | Boolean |

**请求示例**:
```bash
POST /ai/admin/agent/queryAllAgentConfigListByChannel
Content-Type: application/json

{
  "channel": "agent",
  "pageNum": 1,
  "pageSize": 100
}
```

#### 2.2 知识库管理 (RAG Order)
**基础路径**: `/ai/admin/rag/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/rag/queryRagOrderList` | 查询RAG订单列表 | AiClientRagOrder | List<AiClientRagOrder> |
| POST | `/ai/admin/rag/queryAllValidRagOrder` | 查询所有有效知识库 | 无 | List<AiClientRagOrder> |
| POST | `/ai/admin/rag/queryRagOrderDetail` | 查询知识库详情 | AiClientRagOrder | AiClientRagOrder |

**请求示例**:
```bash
POST /ai/admin/rag/queryAllValidRagOrder
Content-Type: application/json

{}
```

#### 2.3 MCP工具管理
**基础路径**: `/ai/admin/client/tool/mcp/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/client/tool/mcp/queryMcpList` | 查询MCP工具列表 | AiClientToolMcp | List<AiClientToolMcp> |
| POST | `/ai/admin/client/tool/mcp/queryMcpDetail` | 查询MCP工具详情 | AiClientToolMcp | AiClientToolMcp |
| POST | `/ai/admin/client/tool/mcp/addMcp` | 新增MCP工具 | AiClientToolMcp | Boolean |
| POST | `/ai/admin/client/tool/mcp/updateMcp` | 更新MCP工具 | AiClientToolMcp | Boolean |
| POST | `/ai/admin/client/tool/mcp/deleteMcp` | 删除MCP工具 | AiClientToolMcp | Boolean |

#### 2.4 客户端工具配置管理
**基础路径**: `/ai/admin/client/tool/config/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/client/tool/config/queryClientToolConfigList` | 查询客户端工具配置列表 | AiClientToolMcp | List<AiClientToolMcp> |
| POST | `/ai/admin/client/tool/config/queryClientToolConfigDetail` | 查询客户端工具配置详情 | AiClientToolMcp | AiClientToolMcp |
| POST | `/ai/admin/client/tool/config/addClientToolConfig` | 新增客户端工具配置 | AiClientToolMcp | Boolean |
| POST | `/ai/admin/client/tool/config/updateClientToolConfig` | 更新客户端工具配置 | AiClientToolMcp | Boolean |
| POST | `/ai/admin/client/tool/config/deleteClientToolConfig` | 删除客户端工具配置 | AiClientToolMcp | Boolean |

#### 2.5 客户端顾问配置管理
**基础路径**: `/ai/admin/client/advisor/config/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/client/advisor/config/queryClientAdvisorConfigList` | 查询客户端顾问配置列表 | AiClientAdvisor | List<AiClientAdvisor> |
| POST | `/ai/admin/client/advisor/config/queryClientAdvisorConfigDetail` | 查询客户端顾问配置详情 | AiClientAdvisor | AiClientAdvisor |
| POST | `/ai/admin/client/advisor/config/addClientAdvisorConfig` | 新增客户端顾问配置 | AiClientAdvisor | Boolean |
| POST | `/ai/admin/client/advisor/config/updateClientAdvisorConfig` | 更新客户端顾问配置 | AiClientAdvisor | Boolean |
| POST | `/ai/admin/client/advisor/config/deleteClientAdvisorConfig` | 删除客户端顾问配置 | AiClientAdvisor | Boolean |

#### 2.6 客户端系统提示词管理
**基础路径**: `/ai/admin/client/system/prompt/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/client/system/prompt/queryClientSystemPromptList` | 查询系统提示词列表 | AiClientSystemPrompt | List<AiClientSystemPrompt> |
| POST | `/ai/admin/client/system/prompt/queryClientSystemPromptDetail` | 查询系统提示词详情 | AiClientSystemPrompt | AiClientSystemPrompt |
| POST | `/ai/admin/client/system/prompt/addClientSystemPrompt` | 新增系统提示词 | AiClientSystemPrompt | Boolean |
| POST | `/ai/admin/client/system/prompt/updateClientSystemPrompt` | 更新系统提示词 | AiClientSystemPrompt | Boolean |
| POST | `/ai/admin/client/system/prompt/deleteClientSystemPrompt` | 删除系统提示词 | AiClientSystemPrompt | Boolean |

#### 2.7 客户端模型配置管理
**基础路径**: `/ai/admin/client/model/config/`

| 方法 | 路径 | 功能 | 参数 | 响应 |
|------|------|------|------|------|
| POST | `/ai/admin/client/model/config/queryClientModelConfigList` | 查询模型配置列表 | AiClientModel | List<AiClientModel> |
| POST | `/ai/admin/client/model/config/queryClientModelConfigDetail` | 查询模型配置详情 | AiClientModel | AiClientModel |
| POST | `/ai/admin/client/model/config/addClientModelConfig` | 新增模型配置 | AiClientModel | Boolean |
| POST | `/ai/admin/client/model/config/updateClientModelConfig` | 更新模型配置 | AiClientModel | Boolean |
| POST | `/ai/admin/client/model/config/deleteClientModelConfig` | 删除模型配置 | AiClientModel | Boolean |

## 数据模型 (Data Models)

### AiAgent (AI代理)
```json
{
  "id": 1,
  "agentId": "1",
  "agentName": "文件系统助手",
  "description": "文件系统操作和管理",
  "channel": "agent",
  "strategy": "flowAgentExecuteStrategy",
  "status": 1,
  "createTime": "2025-06-14T12:41:20",
  "updateTime": "2025-08-24T16:41:24"
}
```

### AiClientRagOrder (知识库配置)
```json
{
  "id": 3,
  "ragId": "9001",
  "ragName": "生成文章提示词",
  "knowledgeTag": "生成文章提示词",
  "status": 1,
  "createTime": "2025-06-14T12:44:56",
  "updateTime": "2025-06-14T12:44:56"
}
```

### AiClientToolMcp (MCP工具)
```json
{
  "id": 8,
  "mcpId": "5003",
  "mcpName": "filesystem",
  "transportType": "stdio",
  "transportConfig": "{\"filesystem\":{\"command\":\"npx\",\"args\":[\"-y\",\"@modelcontextprotocol/server-filesystem\"]}}",
  "requestTimeout": 180,
  "status": 1,
  "createTime": "2025-06-14T12:36:30",
  "updateTime": "2025-07-05T16:31:44"
}
```

## 响应格式

### 成功响应
```json
{
  "code": "0000",
  "info": "成功",
  "data": {}
}
```

### 错误响应
```json
{
  "code": "5000",
  "info": "系统异常",
  "data": null
}
```

## 状态码说明

| 状态码 | 说明 |
|--------|------|
| 0000 | 成功 |
| 0001 | 未知失败 |
| 0002 | 非法参数 |
| 5000 | 系统异常 |

## 跨域配置

所有接口都配置了跨域支持：
```java
@CrossOrigin("*")
```

## 注意事项

1. **请求方式**: 所有接口都使用POST方法
2. **Content-Type**: 通常为 `application/json`
3. **参数传递**: 通过 `@RequestBody` 传递JSON参数
4. **响应格式**: 统一使用 `ResponseEntity` 包装响应
5. **异常处理**: 全局异常处理器统一处理异常
6. **日志记录**: 所有接口都有详细的日志记录

## 测试工具

### Postman 集合
```json
{
  "info": {
    "name": "AI Agent Station Study API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "RAG相关接口",
      "item": [
        {
          "name": "解析Git仓库",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/x-www-form-urlencoded"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/rag/analyze_git_repository",
              "host": ["{{baseUrl}}"],
              "path": ["rag", "analyze_git_repository"]
            },
            "body": {
              "mode": "urlencoded",
              "urlencoded": [
                {
                  "key": "repoUrl",
                  "value": "https://github.com/example/repo"
                },
                {
                  "key": "userName",
                  "value": "username"
                },
                {
                  "key": "token",
                  "value": "token"
                }
              ]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8091"
    }
  ]
}
```

## 更新日志

- **2025-09-15**: 初始版本，包含所有管理接口和业务接口
- **2025-09-15**: 去除所有路径中的 `api/v1` 前缀
- **2025-09-15**: 调整Maven模块编译顺序，infrastructure先于trigger编译
