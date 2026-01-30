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
| `lumos-core`| 核心业务：RAG 编排 (`SearchService`)、智能代理 (`AgenticService`)、知识入库 Pipeline (`KnowledgeService`)、动态 Prompt 管理 (`PromptService`)、意图识别与路由 (`IntentRouterService`)。支持混合检索 (Hybrid Search) 与父子索引切片。 |
| `lumos-infra`| 基础设施：`LumosAiConfiguration` (极简 OpenAI 协议工厂)、`PgVectorStoreAdapter` (混合检索 SQL 实现)、`TikaDocumentParserAdapter` (ETL 解析)、`RecursiveTextSplitterAdapter` (语义切片)、`LocalGuardrailAdapter` (敏感词与 PII 脱敏)。 |

## 4. 核心工作流
- **多租户隔离**:
    - **Documents**: `documents` 表增加 `namespace` 字段，向量检索时通过 `JOIN` 进行物理隔离过滤。
    - **API**: 所有上传与检索接口均支持可选的 `namespace` 参数，默认为 `default`。
- **知识入库 Pipeline (ETL)**:
    1. 用户上传文件 -> `KnowledgeController` 注册文档 (支持 Namespace)。
## 5. 开发与部署
- **Docker 模式 (推荐)**: 运行 `./lumos.sh start`。该脚本集成了 Maven 编译、镜像构建与容器编排。
- **管理脚本**: `lumos.sh` 支持 `start`, `stop`, `restart`, `status`, `logs` 操作。
- **Local 模式 (降级)**: 使用 Profile `local` (`-Dspring.profiles.active=local`)，启动 H2 内存数据库。

