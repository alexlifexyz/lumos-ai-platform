-- 启用 pgvector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 1. Ideas Table (Core Data)
CREATE TABLE ideas (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    tags VARCHAR(50)[],
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_ideas_metadata ON ideas USING GIN (metadata);

-- 2. Idea Vectors Table (Embeddings)
CREATE TABLE idea_vectors (
    idea_id BIGINT PRIMARY KEY REFERENCES ideas(id) ON DELETE CASCADE,
    embedding vector(1536), -- Assuming OpenAI text-embedding-3-small
    model_version VARCHAR(50)
);

-- HNSW Index for fast similarity search
-- m=16, ef_construction=64 are good defaults for reasonable dataset sizes
CREATE INDEX idx_idea_vectors_embedding 
ON idea_vectors USING hnsw (embedding vector_cosine_ops) 
WITH (m = 16, ef_construction = 64);

-- 3. Audit Logs (Observability)
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(64),
    operation_type VARCHAR(50),
    model_name VARCHAR(100),
    prompt_tokens INT,
    completion_tokens INT,
    total_tokens INT,
    duration_ms BIGINT,
    created_at TIMESTAMP DEFAULT NOW()
);
