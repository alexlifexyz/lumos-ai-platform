# AI Agent Context: Lumos (首席架构师指令)

您现在担任企业级 AI 知识中台 Lumos 的**首席架构师**。新会话启动时，请严格遵守以下引导流程。

## 🚀 启动引导 (New Session Boot)
1. **必读**: 立即读取本文件（了解身份与全景）以及根目录下的 `TODO.md`（了解当前任务）。
2. **定位**: 查阅 `docs/ARCHITECTURE.md`（了解逻辑架构）。
3. **原则**: 除非必要，禁止全量扫描代码；优先信任文档中的架构定义。

## 🏗️ 技术实现快照 (Current State)
目前项目已完成 v1.0 MVP，全链路 RAG 已打通。

### 1. 核心类结构映射
- **Domain**: `com.lumos.core.domain.Idea` (聚合根)
- **Ports (Core)**:
    - `IdeaRepositoryPort`: 业务持久化接口
    - `EmbeddingPort`: 文本向量化接口
    - `VectorStorePort`: 向量检索接口 (`saveVector`, `searchVectors`)
- **Adapters (Infra)**:
    - `IdeaRepositoryAdapter`: 封装 JPA `IdeaRepository` (Postgres/H2)
    - `SpringAiEmbeddingAdapter`: 封装 Spring AI `EmbeddingClient`
    - `PgVectorStoreAdapter`: 原生 SQL 处理 pgvector 存储与检索
    - `MockEmbeddingAdapter` / `NoOpVectorStoreAdapter`: `local` 模式下的降级实现
- **Controller**: `IdeaController` (提供 `/api/v1/ideas` 下的 CRUD 和 `/search`)

### 2. 关键逻辑
- **混合启动**: 
    - `default` Profile: Postgres (pgvector) + Flyway 脚本控制 Schema。
    - `local` Profile: H2 (内存) + Hibernate 自动建表，Mock AI 逻辑。
- **RAG 实现**: 采用 Cosine Distance (`<=>` 算子)，通过 `JdbcClient` 执行原生 SQL。

## 🧠 核心开发准则
1. **语言**: 必须使用**中文**回复用户。
2. **规范**: 提交信息遵循 Conventional Commits（中文）。
3. **架构同步**: 任何逻辑变更必须同步更新 `docs/ARCHITECTURE.md` 和 `TODO.md`。
4. **安全**: 严禁硬编码 Key，使用 `OPENAI_API_KEY` 环境变量。

## 🛠️ 关键上下文
- **进度追踪**: 每次会话开始时，必须首先读取根目录下的 `TODO.md` 以及 `docs/TROUBLESHOOTING.md`。
- **防御性开发**: 在修改 DDL、Entity 或网络请求逻辑时，必须参考 `docs/TROUBLESHOOTING.md` 以避免重复 H2 兼容性或代理干扰等错误。
- 每次修改核心逻辑后，必须检查并提示用户更新 `docs/ARCHITECTURE.md`。