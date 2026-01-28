# Lumos API 测试请求参数 (Swagger/Postman)

本文档提供可以直接复制使用的 JSON 请求体，用于验证 Lumos AI 平台的各项核心功能。

---

## 1. 创建 Idea (POST /api/v1/ideas)

### 场景 A：基础全链路验证
用于验证：Qwen 模型向量生成 + PostgreSQL 持久化。

```json
{
  "title": "通义千问集成测试",
  "content": "验证 Lumos 能否通过 OpenAI 兼容协议成功调用阿里 DashScope 生成向量并存入 Postgres。",
  "tags": ["AI", "Qwen", "Test"],
  "metadata": {
    "source": "swagger",
    "priority": "normal"
  }
}
```

### 场景 B：复杂嵌套对象验证
用于验证：JSONB 数据结构的深度存取 + 自动审计时间戳。

```json
{
  "title": "深度 Agent 设计思考",
  "content": "未来的 AI Agent 应该具备自我规划和工具调用的闭环能力。",
  "tags": ["Agent", "Architecture"],
  "metadata": {
    "specs": {
      "complexity": "high",
      "versions": [1.0, 1.1]
    },
    "reviewer": "Alex"
  }
}
```

### 场景 C：参数校验异常验证
用于验证：标题为空时，全局异常处理器是否返回标准化的 400 错误。

```json
{
  "title": "",
  "content": "这里标题为空，应该触发 400 Bad Request 响应"
}
```

---

## 2. 语义搜索 (GET /api/v1/ideas/search)

### 测试查询
直接在 Swagger 的 `query` 参数中输入以下文本进行测试：

- `AI 架构`
- `阿里模型`
- `Agent 规划能力`

---
*提示：执行完 POST 请求后，请注意复制响应中的 `uuid`，以便后续通过 GET 接口查询详情。*
