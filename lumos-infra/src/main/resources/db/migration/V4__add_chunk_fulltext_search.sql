-- V4: 为文档片段增加全文检索支持 (Integrated Search 基建)

-- 1. 增加 tsvector 字段
ALTER TABLE document_chunks ADD COLUMN ts_content tsvector;

-- 2. 创建 GIN 索引以加速全文检索
CREATE INDEX idx_chunks_ts_content ON document_chunks USING GIN (ts_content);

-- 3. 创建函数：将片段内容转换为 tsvector
CREATE OR REPLACE FUNCTION chunks_tsvector_trigger() RETURNS trigger AS $$
BEGIN
  new.ts_content := to_tsvector('simple', coalesce(new.content, ''));
  RETURN new;
END
$$ LANGUAGE plpgsql;

-- 4. 创建触发器：在插入或更新时自动同步 ts_content
CREATE TRIGGER trg_chunks_upsert_tsvector
BEFORE INSERT OR UPDATE ON document_chunks
FOR EACH ROW EXECUTE FUNCTION chunks_tsvector_trigger();

-- 5. 初始化存量数据
UPDATE document_chunks SET ts_content = to_tsvector('simple', coalesce(content, ''));
