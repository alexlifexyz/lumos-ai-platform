-- 给 documents 表添加 namespace 字段，默认为 'default'
ALTER TABLE documents ADD COLUMN namespace VARCHAR(50) DEFAULT 'default' NOT NULL;
CREATE INDEX idx_documents_namespace ON documents(namespace);

-- 同时也给 ideas 表添加 namespace 字段，保持一致性
ALTER TABLE ideas ADD COLUMN namespace VARCHAR(50) DEFAULT 'default' NOT NULL;
CREATE INDEX idx_ideas_namespace ON ideas(namespace);
