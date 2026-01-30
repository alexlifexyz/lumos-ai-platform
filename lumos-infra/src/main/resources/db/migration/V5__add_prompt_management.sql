CREATE TABLE prompts (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 插入默认 Prompt
INSERT INTO prompts (code, name, content, description)
VALUES (
    'DEFAULT_AGENT',
    '默认 AI 助手',
    '你是一个 Lumos 平台的 AI 助手。
你可以回答用户的任何问题，语气应专业、友好且简洁。
最终回复应使用中文。',
    '系统默认的全局助手 Prompt'
);

INSERT INTO prompts (code, name, content, description)
VALUES (
    'INTENT_ROUTER',
    '意图识别专家',
    '你是一个意图识别专家。你的任务是分析用户的输入，并将其归类。请仅输出意图代码（SQL, RAG 或 GENERAL）。',
    '用于判断用户是查数据库还是查知识库的 Router Prompt'
);

INSERT INTO prompts (code, name, content, description)
VALUES (
    'RAG_AGENT',
    '知识库问答助手',
    '你是一个基于知识库的问答助手。请根据提供的【背景知识】来回答用户的问题。最终回复应使用中文。',
    '用于基于知识库内容进行回答的 Prompt'
);

