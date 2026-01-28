# Lumos 项目演进路线图 (Phase 2 & Beyond)

本项目旨在通过 Java 工程化手段解决 AI 落地痛点。

## ✅ Phase 1: 核心基建与 MVP (已完成)
- [x] Maven 多模块 Modular Monolith 架构。
- [x] PostgreSQL (pgvector) + Redis 容器化编排。
- [x] 全链路 RAG 闭环 (存/取/搜)。
- [x] H2/Postgres 双模启动与降级策略。
- [x] 工程化质量保障 (Unit Test, Global Exception, CI)。

## 🚀 Phase 2: 智能代理与 RAG 深度优化 (高优先级)

### 1. 智能代理 (Agentic AI)
- [ ] **Text-to-SQL 专家**: 利用 Spring AI Function Calling 实现自然语言查询数据库。
    - [ ] 定义只读数据库查询工具。
    - [ ] 编写 System Prompt 注入 Schema 上下文。
    - [ ] 实现结果摘要与数据可视化 DTO。

### 2. RAG 检索质量优化
- [ ] **混合检索 (Hybrid Search)**: 结合 Postgres 全文检索 (tsvector) 与向量检索 (pgvector)。
- [ ] **重排序 (Re-ranking)**: 集成外部 Reranker 模型对检索结果进行精排。

## ⚡ Phase 3: 高性能与异步化处理

### 3. 响应式与并发优化
- [ ] **流式响应 (Streaming)**: 使用 Spring WebFlux/SSE 实现 LLM 的打字机输出效果。
- [ ] **异步向量化 (Async Embedding)**: 使用 Spring Events 或消息队列异步处理 Embedding 任务，优化写入性能。

## 🛡️ Phase 4: 企业级治理与可观测性

### 4. AI 审计与监控
- [ ] **Token 审计系统**: 完善 `audit_logs`，记录 Token 消耗、耗时及 Prompt 详情。
- [ ] **成本核算**: 实现按模型、按操作类型的成本统计接口。

### 5. 提示词工程 (Prompt Management)
- [ ] **动态 Prompt 模板**: 将 Prompt 存入 Redis/DB，支持不重启应用动态修改。
- [ ] **内容安全 (Guardrails)**: 实现 Prompt 敏感词脱敏。

---
*注：每完成一项，请及时更新此文件。*
