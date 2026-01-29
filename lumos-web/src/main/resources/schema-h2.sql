CREATE TABLE IF NOT EXISTS ideas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    content CLOB NOT NULL,
    tags VARCHAR(1000),
    metadata VARCHAR(2000),
    ts_content CLOB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS idea_vectors (
    idea_id BIGINT PRIMARY KEY,
    embedding VARCHAR(MAX), -- Store as string in H2
    model_version VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    md5 VARCHAR(32),
    size BIGINT,
    metadata VARCHAR(2000),
    status VARCHAR(50),
    failure_reason CLOB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id BIGINT NOT NULL,
    content CLOB NOT NULL,
    chunk_index INT NOT NULL,
    metadata VARCHAR(2000),
    ts_content CLOB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS chunk_vectors (
    chunk_id BIGINT PRIMARY KEY,
    embedding VARCHAR(MAX),
    model_version VARCHAR(50)
);