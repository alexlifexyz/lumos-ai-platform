-- V3: 知识管理增强 (Documents & Chunks)

-- 1. 文档表：存储源文件元数据
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    md5 VARCHAR(32),
    size BIGINT,
    metadata JSONB DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'PENDING',
    failure_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. 知识片段表：存储切分后的文本块
CREATE TABLE document_chunks (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    chunk_index INT NOT NULL,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 3. 向量存储扩展：为 Chunk 增加向量支持
-- 注意：这里复用或参考之前的设计，如果后续需要独立检索 Chunk，可以增加关联索引
CREATE TABLE chunk_vectors (
    chunk_id BIGINT PRIMARY KEY REFERENCES document_chunks(id) ON DELETE CASCADE,
    embedding vector(1536), 
    model_version VARCHAR(50)
);

CREATE INDEX idx_chunk_vectors_embedding ON chunk_vectors USING hnsw (embedding vector_cosine_ops);
