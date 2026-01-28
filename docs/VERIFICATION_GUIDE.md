# Lumos 功能验证手册 (MVP)

## 1. 启动环境
全容器模式：
```bash
mvn clean package -DskipTests
docker compose up -d
```

## 2. 核心功能测试

### A. 创建 Idea (测试写入、JSONB/Array存储、JPA审计)
```bash
curl -X POST http://localhost:8080/api/v1/ideas \
  -H "Content-Type: application/json" \
  -d '{ \
    "title": "测试点子", \
    "content": "这是一个关于 AI 的深度思考。", \
    "tags": ["AI", "Test"], \
    "metadata": {"source": "manual"} \
  }'
```

### B. 语义搜索 (测试 RAG 链路)
```bash
curl "http://localhost:8080/api/v1/ideas/search?query=AI思考&limit=5"
```

## 3. 环境配置 (API Key)
参考根目录下的 `.env.example`。如需使用真实 OpenAI 功能，需创建 `.env` 并配置 `OPENAI_API_KEY`。

```