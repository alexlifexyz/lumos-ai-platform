<!--
[AI INSTRUCTION]
此文件是项目的核心架构地图。
规则：
1. 每次进行涉及文件结构变更、核心逻辑修改或 API 接口变动时，必须同步更新此文件。
2. 保持技术栈、模块职责和核心业务流的准确性。
3. 在任务结束前的 Finalize 阶段，请检查此文件是否需要更新。
-->

# System Architecture: Lumos AI Platform

> **生成日期**: 2026-01-27
> **版本**: 1.0.0
> **状态**: 初始化

## 1. 项目概况
Lumos 是一个企业级 AI 知识与数据中台，基于 Java 生态构建，核心功能包括 RAG (检索增强生成) 和基于 Agent 的数据分析。

## 2. 技术栈
- **Backend**: Java 17, Spring Boot 3.2.5, Spring AI 0.8.1
- **Database**: PostgreSQL 16 + pgvector (向量存储)
- **AI Protocol**: 统一采用 OpenAI 兼容协议，支持 OpenAI, Kimi, 智谱, 千问等。
- **Infra**: Docker, Redis

## 3. 模块职责 (Maven Multi-Module)
| 模块 | 职责 |
|------|------|
| `lumos-api` | 共享内核：DTOs, Exceptions, 公共工具类。 |
| `lumos-core`| 核心业务：RAG 编排 (`SearchService`)、Domain Models。支持混合检索 (Hybrid Search)。 |
| `lumos-infra`| 基础设施：`LumosAiConfiguration` (极简 OpenAI 协议工厂)、`PgVectorStoreAdapter` (混合检索 SQL 实现)。 |
| `lumos-web`  | Web 入口：REST API、Swagger、全局异常处理。 |

## 4. 核心工作流
- **混合检索流**: 
    1. 用户查询 -> 生成 Embedding。
    2. 执行混合 SQL：`(0.7 * 向量相似度) + (0.3 * 全文检索得分)`。
    3. 结合 `tsvector` 与 `pgvector` 确保召回率。
- **启动弹性**: 仅需 `OPENAI_API_KEY`、`OPENAI_BASE_URL`、`OPENAI_MODEL` 三个变量即可适配主流厂商。未配置时自动降级为 Mock 模式。

## 5. 后续规划
详细的待办事项和技术演进路线请参考根目录下的 [TODO.md](../TODO.md)。

## 6. 避坑指南与工程规范
为确保项目在异构环境下的一致性，所有开发者必须遵守 [TROUBLESHOOTING.md](TROUBLESHOOTING.md) 中记录的准则。

## 5. 开发与部署
- **Docker 模式 (推荐)**: 运行 `docker-compose up`，使用 Postgres + pgvector。
- **Local 模式 (降级)**: 使用 Profile `local` (`-Dspring.profiles.active=local`)，启动 H2 内存数据库。
  - **注意**: 此模式下向量检索功能不可用（或降级为 Mock），仅用于调试基础业务逻辑。

