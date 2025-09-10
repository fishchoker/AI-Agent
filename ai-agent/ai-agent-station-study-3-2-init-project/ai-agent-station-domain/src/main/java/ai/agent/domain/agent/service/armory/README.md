### Armory 组件说明（agent/service/armory）

Armory 负责基于策略树（Strategy Tree）的方式，按节点组装并启动 AI Agent 的运行时能力，包括模型、顾问（Advisor）、工具（如 MCP）等。该目录中的代码以“节点 + 工厂 + 动态上下文”的模式协同工作，支持可插拔、可扩展的装配能力。

---

### 目录结构

- `AbstractArmorySupport.java`: Armory 抽象基类，封装策略路由、动态 Bean 注册等通用能力。
- `factory/DefaultArmoryStrategyFactory.java`: 提供装配策略所需的上下文 `DynamicContext` 与默认策略工厂。
- `factory/element/RagAnswerAdvisor.java`: 自定义的 RAG 策略顾问实现（基于向量库检索）。
- `node/RootNode.java`: 策略树根节点。
- `node/AiClientNode.java`: AI 客户端装配节点（总控）。
- `node/AiClientAdvisorNode.java`: 顾问（Advisor）装配节点（如 ChatMemory、RAG Answer）。
- `node/AiClientModelNode.java`: 模型装配节点。
- `node/AiClientToolMcpNode.java`: 工具/MCP 装配节点。

---

### 核心概念与职责

- **AbstractArmorySupport**
  - 继承 `AbstractMultiThreadStrategyRouter<AiAgentEngineStarterEntity, DynamicContext, String>`。
  - 提供模板方法：`doApply(...)`、`router(...)`、`get(...)`（在各节点实现中使用）。
  - 封装动态 Bean 注册能力：`registerBean(String beanName, Class<T> beanClass, T beanInstance)`，便于在装配过程中将实例注册到 Spring 容器。
  - 依赖注入：`ApplicationContext`、`ThreadPoolExecutor`、`IAgentRepository`。

- **DynamicContext（策略上下文）**
  - 由 `DefaultArmoryStrategyFactory` 提供，用于在节点间传递装配所需的动态参数（如顾问列表、模型参数、工具配置等）。
  - 典型键值：`aiClientAdvisorList`（顾问配置列表）。

- **Node（节点）**
  - 单一职责，小步快跑；每个节点组装对应职责的组件，并决定下一个节点。
  - 例如 `AiClientAdvisorNode`：读取顾问配置，构建 `Advisor` 并注册为 Spring Bean，然后路由到下一个节点 `AiClientModelNode`。

---

### 运行流程（示意）

1) 入口节点（如 `RootNode` 或 `AiClientNode`）接收 `AiAgentEngineStarterEntity` 与 `DynamicContext`。
2) 读取上下文配置，按装配顺序依次路由到具体节点：Advisor → Model → Tool/MCP …
3) 各节点执行各自的 `doApply(...)`，完成装配并动态注册 Bean。
4) 所有节点装配完成后，返回最终的启动标识或 Bean 名称以供后续业务调用。

---

### 动态 Bean 注册

`AbstractArmorySupport` 提供线程安全的注册方法用于在运行时往 Spring 容器中注入装配产物：

```java
protected synchronized <T> void registerBean(String beanName, Class<T> beanClass, T beanInstance) {
    DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass, () -> beanInstance);
    BeanDefinition beanDefinition = builder.getRawBeanDefinition();
    beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
    if (beanFactory.containsBeanDefinition(beanName)) {
        beanFactory.removeBeanDefinition(beanName);
    }
    beanFactory.registerBeanDefinition(beanName, beanDefinition);
}
```

节点中常见的用法（以 `AiClientAdvisorNode` 为例）：

```java
Advisor advisor = createAdvisor(config);
registerBean("AiClientAdvisor_" + config.getId(), Advisor.class, advisor);
```

---

### 顾问（Advisor）装配示例：`AiClientAdvisorNode`

- 从 `DynamicContext` 读取 `aiClientAdvisorList`。
- 支持的顾问类型示例：
  - `ChatMemory`：基于 `MessageWindowChatMemory` 的对话记忆。
  - `RagAnswer`：基于向量库 `VectorStore` 的检索增强生成（RAG）。

伪代码：

```java
List<AiClientAdvisorVO> list = dynamicContext.getValue("aiClientAdvisorList");
for (AiClientAdvisorVO cfg : list) {
    Advisor advisor = switch (cfg.getAdvisorType()) {
        case "ChatMemory" -> new PromptChatMemoryAdvisor(MessageWindowChatMemory.builder()
                .maxMessages(cfg.getChatMemory().getMaxMessages())
                .build());
        case "RagAnswer" -> new RagAnswerAdvisor(vectorStore, SearchRequest.builder()
                .topK(cfg.getRagAnswer().getTopK())
                .filterExpression(cfg.getRagAnswer().getFilterExpression())
                .build());
        default -> throw new IllegalArgumentException("advisorType not exist");
    };
    registerBean("AiClientAdvisor_" + cfg.getId(), Advisor.class, advisor);
}
return router(requestParameter, dynamicContext);
```

---

### 扩展指南

- 新增顾问类型（Advisor）
  1. 在 `factory/element` 下新增实现类，封装具体能力。
  2. 在 `AiClientAdvisorNode#createAdvisor(...)` 中增加分支，返回新实现。
  3. 在 `DynamicContext` 中提供新类型所需的配置字段。

- 新增节点（Node）
  1. 继承 `AbstractArmorySupport`，实现 `doApply(...)` 和 `get(...)`（返回下一个节点）。
  2. 在上游节点的 `get(...)` 中路由到该节点。
  3. 使用 `registerBean(...)` 动态注册产物（如模型客户端、工具适配器等）。

- 并行化/多线程
  - 可在重写的 `multiThread(...)` 中实现多线程装配，提升大规模组件初始化的效率。

---

### 依赖与环境

- Spring Framework（`ApplicationContext`、BeanFactory 等）
- Spring AI（`Advisor`、`VectorStore` 等）
- Lombok（`@Slf4j`）
- Jakarta（`@Resource`）
- 自研/三方：`cn.bugstack.wrench.design.framework.tree`（策略树框架）

---

### 注意事项

- Bean 名称应保持可预测与唯一（可使用前缀 + 业务主键的方式，如 `AiClientAdvisor_{id}`）。
- 动态注册前建议先删除已存在的 Bean 定义，避免重复注册导致启动异常。
- 节点应避免包含过多业务逻辑，复杂业务放入领域服务或工厂中处理。
- `DynamicContext` 的 Key 需统一管理，避免字符串硬编码分散导致维护困难。

---

### 快速检查清单

- [ ] `DynamicContext` 是否包含本次装配所需的全部参数？
- [ ] 各节点 `doApply(...)` 是否只处理本节点单一职责？
- [ ] 是否为产物注册了正确的 Bean 名称与类型？
- [ ] 路由 `get(...)` 是否指向了下一个正确节点？
- [ ] 是否需要并行装配以提升启动性能？



