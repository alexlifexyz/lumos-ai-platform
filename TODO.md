# Lumos 项目演进路线图

本项目旨在通过 Java 工程化手段解决 AI 落地痛点。

## ✅ 已完成阶段 (Phases 1-5)

### Phase 1-3: 核心 RAG 基建
- [x] **架构**: Maven 多模块 (Modular Monolith)、H2/Postgres 双模支持。
- [x] **检索**: pgvector (向量) + tsvector (全文) 混合检索，Jina AI 重排序。
- [x] **交互**: SSE 流式响应 (打字机效果)，业务与 AI SDK 深度解耦。

### Phase 4: 企业级治理与审计
- [x] **全异步审计**: AOP + Spring Events 实现零阻塞审计。
- [x] **全链路追踪**: 支持流式 Token 统计与 TraceID 链路追踪。
- [x] **Token 兜底**: 集成 JTokkit 本地估算器。

### Phase 5: 知识库 ETL 与全域搜索
- [x] **智能解析**: 集成 Apache Tika，支持 PDF/Word 解析与清洗。
- [x] **语义切片**: 递归字符切分 (RecursiveTextSplitter)，支持语义重叠。
- [x] **父子索引**: `Document` -> `Chunk` 层级存储结构。
- [x] **全域集成搜索**: 统一检索 `Idea` 和 `Document Chunk`，支持来源回溯。

---

## 🚀 待办计划 (Backlog for Next Session)

### Phase 6: Agent 深度编排与安全 (已完成)
- [x] **动态 Prompt 管理**: 
    - 将硬编码的 System Prompt 迁移至数据库。
    - 支持通过 API 热更新 Prompt 模板（无需重启）。
- [x] **内容安全 (Guardrails)**: 
    - 输入/输出敏感词过滤 (Toxicity Check)。
    - PII (个人隐私信息) 自动脱敏。
- [x] **多 Agent 协作 (Router)**:
    - 实现“意图识别 Agent”，自动判断用户意图是查数据库 (SQL) 还是查文档 (RAG)。

### Phase 7: 高级 RAG 策略 (已完成)
- [x] **多知识库隔离 (Multi-Tenancy)**: 支持不同用户/租户拥有独立的知识库空间。
- [x] **父子检索增强 (Parent Document Retrieval)**: 命中 Chunk 后，自动召回其父文档或前后文窗口（Window Retrieval）。
- [ ] **图谱增强 (GraphRAG)**: 尝试提取实体关系构建知识图谱（可选，推迟）。

### Phase 8: 前端与可视化
- [ ] **审计仪表盘**: 可视化展示 Token 消耗趋势与模型成本。
- [ ] **知识库管理 UI**: 文件上传、切片预览、解析状态监控。

---
*注：新会话启动时，请优先从 Phase 6 开始规划。*
