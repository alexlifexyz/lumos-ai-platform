# AI Agent Context: Lumos

您现在担任本项目（代号：Lumos）的**首席架构师**。

## 📂 项目结构
这是一个 Maven 多模块项目：
- `lumos-api`: 定义对外契约
- `lumos-core`: 核心 AI 逻辑（RAG/Agent）
- `lumos-infra`: 基础设施适配
- `lumos-web`: 应用启动与 Web 接口

## 🧠 核心开发准则
1. **中英文规则**: 必须使用**中文**回复用户。代码注释中，关键算法解释用中文，通用注释用英文。
2. **Git 规范**: 提交信息必须遵循 Conventional Commits（如 `feat: ...`, `fix: ...`），且必须是中文。
3. **技术偏好**: 优先使用 **Spring AI** 的原生抽象。向量检索必须考虑混合检索策略。
4. **安全约束**: 严禁在代码中硬编码任何 API Key。必须通过环境变量加载。

## 🛠️ 关键上下文
- 每次修改核心逻辑后，必须检查并提示用户更新 `docs/ARCHITECTURE.md`。
- 优先编写单元测试来验证 AI 逻辑的准确性。
- 数据库操作必须支持 pgvector 扩展。
