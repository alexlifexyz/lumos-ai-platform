# Lumos AI Platform (v1.0 MVP) - 交付物清单

**项目状态**: 核心功能完备 (Core Functionality Complete)
**交付日期**: 2026-01-27
**版本**: 1.0.0
**代码仓库**: [https://github.com/alexlifexyz/lumos-ai-platform](https://github.com/alexlifexyz/lumos-ai-platform)

---

## 1. 核心工件 (Artifacts)

| 类型 | 文件/模块 | 描述 |
| :--- | :--- | :--- |
| **可执行 JAR** | `lumos-web/target/lumos-web-1.0.0-SNAPSHOT.jar` | 包含所有依赖的完整应用包，可直接运行。 |
| **数据库脚本** | `lumos-infra/.../db/migration/V1__init_schema.sql` | 包含 `ideas` (JSONB) 和 `idea_vectors` (HNSW 索引) 的完整 Schema。 |
| **API 文档** | Swagger UI (运行时生成) | 访问路径: `/swagger-ui/index.html`。 |
| **架构文档** | `docs/ARCHITECTURE.md` | 系统设计的真理来源，记录了技术栈、模块职责和核心工作流。 |
| **自动化流水线** | `.github/workflows/ci.yml` | 每次推送自动执行编译与单元测试。 |

## 2. 功能清单 (Capabilities)

### ✅ 已实现功能
1.  **Idea 管理 (CRUD)**
    *   创建灵感 (`POST /api/v1/ideas`)：自动校验参数，支持 `JSONB` 格式的元数据。
    *   查询详情 (`GET /api/v1/ideas/{uuid}`)。
2.  **RAG 检索 (AI Search)**
    *   **向量生成**：集成 Spring AI `EmbeddingClient`。
    *   **语义搜索** (`GET /api/v1/ideas/search`)：基于 PostgreSQL `vector` 扩展的余弦相似度检索。
    *   **自动化链路**：实现“数据写入 -> 自动向量化 -> 持久化”全自动流程。
3.  **双模启动 (Dual-Mode Boot)**
    *   **Docker 模式**：完整体验，使用 Postgres + pgvector。
    *   **Local 模式**：极速启动，使用 H2 内存数据库 + Mock AI 实现。
4.  **工程化规范**
    *   **异常处理**：全局 `@ControllerAdvice`，返回标准 `ProblemDetail` 格式。
    *   **质量保障**：核心业务 Service 单元测试覆盖。
    *   **架构守护**：集成 `pre-commit` 钩子，强制保持代码与架构文档同步。

### ⏳ 规划中功能 (Roadmap)
- [ ] **Agent 增强**：基于 Function Calling 的 Text-to-SQL 数据分析。
- [ ] **异步化**：使用 Spring Events 或 MQ 异步处理向量化流程。
- [ ] **性能优化**：Redis 缓存 Prompt 与 Session。

## 3. 运行指南 (Run Guide)

### 3.1 环境要求
- JDK 17+
- Maven 3.8+
- Docker (运行 Postgres 模式必选)

### 3.2 启动命令
```bash
# 模式 A: 完整 RAG 模式 (推荐)
docker compose up -d
mvn spring-boot:run -pl lumos-web

# 模式 B: Local 开发模式 (无依赖)
mvn spring-boot:run -pl lumos-web -Dspring-boot.run.profiles=local
```

---
*Lumos Architecture Team - 阶段性交付记录*
