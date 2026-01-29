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
| `lumos-core`| 核心业务：RAG 编排 (`SearchService`)、智能代理 (`AgenticService`)、Domain Models。支持混合检索 (Hybrid Search)。 |
| `lumos-infra`| 基础设施：`LumosAiConfiguration` (极简 OpenAI 协议工厂)、`PgVectorStoreAdapter` (混合检索 SQL 实现)、`DatabaseAgentConfig` (Text-to-SQL 工具)。 |
| `lumos-web`  | Web 入口：REST API、Swagger、全局异常处理。 |

## 4. 核心工作流
- **混合检索流**: 
    1. 用户查询 -> 生成 Embedding。
    2. 执行混合 SQL：`(0.7 * 向量相似度) + (0.3 * 全文检索得分)`。
    3. 利用 `ts_content` 字段与预设 Trigger 确保 `tsvector` 与内容实时同步，显著提升检索召回率与性能。
    4. 结果集通过 `IdeaRepositoryAdapter` 进行顺序还原，确保相关性排名准确。
- **智能代理 (Text-to-SQL)**:
    1. 用户自然语言请求 -> `AgenticService` 构造 Prompt。
    2. LLM 通过 Function Calling 调用 `databaseQueryTool`。
    3. 系统自动识别并清洗 Base URL（移除重复的 `/v1`），确保请求路由正确。
    4. 容器内屏蔽环境变量代理干扰，确保 AI API 通讯纯净。
    5. 执行只读 SQL 并由 LLM 生成人类可读的回答。

## 5. 开发与部署
- **Docker 模式 (推荐)**: 运行 `./lumos.sh start`。该脚本集成了 Maven 编译、镜像构建与容器编排。
- **管理脚本**: `lumos.sh` 支持 `start`, `stop`, `restart`, `status`, `logs` 操作。
- **Local 模式 (降级)**: 使用 Profile `local` (`-Dspring.profiles.active=local`)，启动 H2 内存数据库。

