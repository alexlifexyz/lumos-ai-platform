# 贡献指南 (Contributing)

欢迎参与 Lumos AI Platform 的建设！为了保持项目的高质量和架构的一致性，请遵守以下指南：

## 🛠 开发规范

1. **架构文档同步**：
   本项目采用 Modular Monolith (模块化单体) 架构。如果在开发中引入了新的依赖、调整了模块职责、或是修改了核心的业务流，**必须同步更新 `docs/ARCHITECTURE.md`**。

2. **本地测试与代码检查**：
   在执行 `git commit` 之前，请确保通过了所有的 `pre-commit` 检查，并且在本地能顺利运行所有单元测试：
   ```bash
   mvn clean test
   ```

3. **严格遵循依赖倒置原则 (DIP)**：
   - `lumos-core` 模块必须保持纯净的领域逻辑，**不得直接引用** `lumos-infra` 或第三方底层框架（如特定的数据库驱动库）。
   - 所有的外部依赖（如数据库、Redis、具体的 AI SDK）都必须在 `lumos-core` 中以接口 (Port) 形式定义，并在 `lumos-infra` 中进行实现 (Adapter)。

## 📝 提交信息规范 (Commit Messages)

请尽量使用约定式提交规范 (Conventional Commits)，以便生成清晰的 Changelog：
- `feat:` 新增功能
- `fix:` 修复 Bug
- `docs:` 仅针对文档（README, ARCHITECTURE 等）的修改
- `style:` 格式化、去除多余空格等不影响代码运行逻辑的修改
- `refactor:` 代码重构（不属于新增功能或修复 Bug 的重构操作）
- `test:` 添加或修改测试用例
- `chore:` 构建过程、依赖项更新或辅助工具的变动

再次感谢您的贡献！