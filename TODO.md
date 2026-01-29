# Lumos 项目演进路线图 (Phase 5 & 6)

本项目旨在通过 Java 工程化手段解决 AI 落地痛点。

## ✅ Phase 1: 核心基建与 MVP (已完成)
- [x] Maven 多模块架构、Postgres/Redis 容器化、H2 降级策略。

## ✅ Phase 2: 检索深度优化 (已完成)
- [x] **混合检索 (Hybrid Search)**: pgvector + tsvector 融合。
- [x] **重排序 (Re-ranking)**: 集成 Jina AI Reranker 适配器。

## ✅ Phase 3: 交互体验优化 (已完成)
- [x] **流式响应 (Streaming)**: SSE 实现打字机效果。
- [x] **架构解耦**: 引入 `ChatPort` 屏蔽 AI SDK 差异。

## ✅ Phase 4: 企业级治理 (已完成)
- [x] **全异步审计系统**: AOP + Spring Events 实现 Token 统计。
- [x] **流式审计支持**: 实时拦截 Flux 流并计算 Usage。
- [x] **治理接口**: 提供 `/admin/audit/stats` 统计看板。

## 🚀 Phase 5: 高质量数据处理 (当前目标)
- [ ] **多格式解析 (ETL)**: 集成 Apache Tika 提取 PDF/Docx 内容。
- [ ] **语义切片 (Smart Chunking)**: 替代固定长度拆分，基于 Markdown 层级和语义段落。
- [ ] **多知识库隔离**: 支持用户创建独立库，配置不同 Embedding 策略。

## 🛡️ Phase 6: Agent 编排与安全围栏 (后续计划)
- [ ] **动态 Prompt 模板**: 支持 Redis/DB 存储与热动态加载。
- [ ] **内容安全 (Guardrails)**: 输入脱敏与输出合规过滤。
- [ ] **多 Agent 协作**: 任务自动路由与拆解。

---
*注：严格遵守“规划先行”准则。*