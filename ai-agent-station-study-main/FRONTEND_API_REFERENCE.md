### 前端对接接口与实体字段参考

说明：以下接口均已在后端实现。若新增了路由，请重启后端以加载映射。更新类接口必须包含主键 id。

## 会话接口
- POST `/ai/agent/chat_stream`
  - Body: `{ agentId|string|number, message|string, sessionId|string|number, maxStep?:number }`
  - 返回: Server-Sent Events 流

## Agent 管理 `/ai/admin/agent/`
- POST `queryAiAgentList` → `AiAgent[]`
- POST `queryAllAgentConfigListByChannel` (Body: `{ channel?: string }`) → `AiAgent[]`
- POST `queryAiAgentDetail` (Body: `{ id:number }`) → `AiAgent`
- GET  `queryAiAgentDetail?id` → `AiAgent`
- GET  `queryAiAgentById?id` → `AiAgent`
- POST `addAiAgent` (Body: `AiAgent`) → `boolean`
- POST `updateAiAgent` (Body: `AiAgent`) → `boolean`
- POST `deleteAiAgent` (Body: `{ id:number }`) → `boolean`

## 任务调度 `/ai/admin/agent/task/schedule/`
- POST `queryTaskScheduleList` (Body: `{ agentId?:number }`) → `AiAgentTaskSchedule[]`
- POST `queryTaskScheduleListByAgentId` (Body: `{ agentId:number }`) → `AiAgentTaskSchedule[]`
- POST `queryEnabledTaskScheduleList` → `AiAgentTaskSchedule[]`
- POST `addTaskSchedule` (Body: `AiAgentTaskSchedule`) → `boolean`
- POST `updateTaskSchedule` (Body: `AiAgentTaskSchedule`) → `boolean`
- POST `deleteTaskSchedule` (Body: `{ id:number }`) → `boolean`
- GET  `queryTaskScheduleById?id` → `AiAgentTaskSchedule`

## 客户端管理 `/ai/admin/agent/client/`
- POST `queryAgentClientList` (Body: `{ clientName?:string }`) → `AiClient[]`
- POST `queryClientDetail` (Body: `{ clientId:string }`) → `AiClient`
- POST `queryEnabledClientList` → `AiClient[]`
- POST `addClient` (Body: `AiClient`) → `boolean`
- POST `updateClient` (Body: `AiClient`) → `boolean`
- POST `deleteClient` (Body: `{ clientId:string }`) → `boolean`
- GET  `client/queryAgentClientById?id` → `AiClient`

## 顾问管理 `/ai/admin/client/advisor/`
- POST `queryClientAdvisorList` (Body: `{ advisorType?:string, status?:number }`) → `AiClientAdvisor[]`
- POST `queryAdvisorDetail` (Body: `{ id:number }`) → `AiClientAdvisor`
- POST `queryEnabledAdvisorList` → `AiClientAdvisor[]`
- POST `addAdvisor` (Body: `AiClientAdvisor`) → `boolean`
- POST `updateClientAdvisor` (或 `advisor/updateClientAdvisor`) (Body: `AiClientAdvisor`) → `boolean`
- POST `deleteAdvisor` (Body: `{ id:number }`) → `boolean`
- GET  `queryClientAdvisorById?id` → `AiClientAdvisor`

## 顾问配置 `/ai/admin/client/advisor/config/`
- POST `queryClientAdvisorConfigList` (Body: `AiClientAdvisor`) → `AiClientAdvisor[]`
- POST `queryClientAdvisorConfigDetail` (Body: `{ id:number }`) → `AiClientAdvisor`
- POST `addClientAdvisorConfig` (Body: `AiClientAdvisor`) → `boolean`
- POST `updateClientAdvisorConfig` (Body: `AiClientAdvisor`) → `boolean`
- POST `deleteClientAdvisorConfig` (Body: `{ id:number }`) → `boolean`
- GET  `queryClientAdvisorConfigById?id` → `AiClientAdvisor`

## 模型管理 `/ai/admin/client/model/`
- POST `queryClientModelList` (Body: `{ modelType?:string, apiId?:string }`) → `AiClientModel[]`
- POST `queryModelDetail` (Body: `{ id:number }`) → `AiClientModel`
- POST `queryEnabledModelList` → `AiClientModel[]`
- POST `addModel` (Body: `AiClientModel`) → `boolean`
- POST `updateModel` (Body: `AiClientModel`) → `boolean`
- POST `deleteModel` (Body: `{ id:number }`) → `boolean`
- GET  `queryClientModelById?id` → `AiClientModel`

## 模型配置 `/ai/admin/client/model/config/`
- POST `queryClientModelConfigList` (Body: `AiClientModel`) → `AiClientModel[]`
- POST `queryClientModelConfigDetail` (Body: `{ id:number }`) → `AiClientModel`
- POST `addClientModelConfig` (Body: `AiClientModel`) → `boolean`
- POST `updateClientModelConfig` (Body: `AiClientModel`) → `boolean`
- POST `deleteClientModelConfig` (Body: `{ id:number }`) → `boolean`
- GET  `queryClientModelConfigById?id` → `AiClientModel`

## 系统提示词 `/ai/admin/client/system/prompt/`
- POST `queryAllSystemPromptConfig` → `AiClientSystemPrompt[]`
- POST `querySystemPromptConfigDetail` (Body: `{ id:number }`) → `AiClientSystemPrompt`
- POST `addSystemPromptConfig` (Body: `AiClientSystemPrompt`) → `boolean`
- POST `updateSystemPrompt` (或 `updateSystemPromptConfig`) (Body: `AiClientSystemPrompt`) → `boolean`
- POST `deleteSystemPromptConfig` (Body: `{ id:number }`) → `boolean`
- GET  `querySystemPromptById?id` → `AiClientSystemPrompt`

## MCP 工具 `/ai/admin/client/tool/mcp/`
- POST `queryMcpList` (Body: `AiClientToolMcp`) → `AiClientToolMcp[]`
- POST `queryMcpDetail` (Body: `{ id:number }`) → `AiClientToolMcp`
- POST `addMcp` (Body: `AiClientToolMcp`) → `boolean`
- POST `updateMcp` (Body: `AiClientToolMcp`) → `boolean`
- POST `deleteMcp` (Body: `{ id:number }`) → `boolean`
- GET  `queryMcpById?id` → `AiClientToolMcp`

## 客户端工具配置 `/ai/admin/client/tool/config/`
- POST `queryClientToolConfigList` (Body: `AiClientToolMcp`) → `AiClientToolMcp[]`
- POST `queryClientToolConfigDetail` (Body: `{ id:number }`) → `AiClientToolMcp`
- POST `addClientToolConfig` (Body: `AiClientToolMcp`) → `boolean`
- POST `updateClientToolConfig` (Body: `AiClientToolMcp`) → `boolean`
- POST `deleteClientToolConfig` (Body: `{ id:number }`) → `boolean`

## RAG 订单 `/ai/admin/rag/`
- POST `queryRagOrderList` (Body: `AiClientRagOrder`) → `AiClientRagOrder[]`
- POST `queryAllValidRagOrder` → `AiClientRagOrder[]`
- POST `queryRagOrderDetail` (Body: `{ id:number }`) → `AiClientRagOrder`
- POST `addRagOrder` (Body: `AiClientRagOrder`) → `boolean`
- POST `updateRagOrder` (Body: `AiClientRagOrder`) → `boolean`
- POST `deleteRagOrder` (Body: `{ id:number }`) → `boolean`
- GET  `queryRagOrderById?id` → `AiClientRagOrder`

## 实体字段（常用）
- AiAgent: `{ id:number, agentId:string, agentName:string, description?:string, channel?:string, strategy?:string, status?:number, createTime?:string, updateTime?:string }`
- AiAgentTaskSchedule: `{ id:number, agentId:string, taskName:string, description?:string, cronExpression:string, taskParam?:string, status:number, createTime?:string, updateTime?:string }`
- AiClient: `{ id:number, clientId:string, clientName:string, description?:string, status:number, createTime?:string, updateTime?:string }`
- AiClientAdvisor: `{ id:number, advisorName:string, advisorType?:string, orderNum?:number, extParam?:string, status:number, createTime?:string, updateTime?:string }`
- AiClientModel: `{ id:number, modelName?:string, modelType?:string, apiId?:string, status:number, createTime?:string, updateTime?:string }`
- AiClientSystemPrompt: `{ id:number, promptName?:string, content?:string, status:number, createTime?:string, updateTime?:string }`
- AiClientToolMcp: `{ id:number, mcpId?:string, mcpName?:string, transportType?:string, transportConfig?:string, requestTimeout?:number, status:number, createTime?:string, updateTime?:string }`
- AiClientRagOrder: `{ id:number, orderName?:string, status:number, createTime?:string, updateTime?:string }`


