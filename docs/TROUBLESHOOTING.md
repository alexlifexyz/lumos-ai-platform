# Lumos 技术排惊与避坑指南 (Troubleshooting)

本文档记录了项目开发过程中遇到的核心工程难题及其解决方案，旨在防止“二次踩坑”。

## 1. 数据库兼容性：H2 vs PostgreSQL

### 问题描述
在 `local` 模式下，Hibernate 6 尝试在 H2 数据库中创建包含 `Map` 或 `List` 类型的表时，会自动将其映射为 `JSONB` 或 `ARRAY`。即便开启了 `MODE=PostgreSQL`，H2 (v2.2+) 依然无法识别这些 Postgres 特有的 DDL 语法，导致 `Table not found` 错误。

### 避坑准则
- **禁止在 Entity 中使用硬编码的 `columnDefinition`**: 除非该 Entity 永远不会在 H2 下运行。
- **使用 AttributeConverter 实现跨库兼容**:
    - 对于 `Map<String, Object>`，使用自定义的 `JsonConverter` 将其序列化为 `TEXT`。
    - 对于 `List<String>`，使用 `ListConverter` 序列化为逗号分隔字符串或 JSON。
- **手动控制 H2 DDL**:
    - 不要依赖 `ddl-auto: update/create` 处理复杂类型。
    - 在 `local` 模式下，通过 `src/main/resources/schema-h2.sql` 显式定义 H2 能接受的表结构（使用 `CLOB` 和 `VARCHAR`）。

## 2. 环境干扰：代理服务器 (Proxy)

### 问题描述
在本地执行 `curl` 验证接口或运行自动化脚本时，可能会遇到 `500 Internal Privoxy Error` 或类似的代理报错。这是因为本地环境配置了全局代理，且代理尝试转发了对 `localhost:8080` 的请求。

### 避坑准则
- 在命令行执行验证脚本时，务必加上 `no_proxy` 环境变量或 `curl --noproxy "*"` 参数。

## 3. Maven 多模块依赖传递

### 问题描述
在 Modular Monolith 架构中，`lumos-web` 虽然通过 `lumos-infra` 间接依赖了 `lumos-api`，但有时会出现 `ClassNotFound` 的编译错误。

### 避坑准则
- **显式依赖原则**: `web` 模块作为启动入口和聚合层，应当在 `pom.xml` 中显式声明对 `api` 和 `core` 的直接依赖，不要完全依赖 Maven 的传递特性。

## 4. Spring Data JPA 扫描路径

### 问题描述
Repository 定义在 `infra` 模块，而启动类在 `web` 模块，导致 Bean 无法被自动注入。

### 避坑准则
- 在启动类上必须显式配置 `@EntityScan` 和 `@EnableJpaRepositories`，并精确指定基础包路径（如 `com.lumos.infra`）。

---
*保持更新：任何耗时超过 30 分钟的 Debug 过程都值得记录在此。*
