-- V2: 为 Ideas 表增加全文检索支持 (Hybrid Search 基建)

-- 1. 增加 tsvector 字段，用于存储分词后的检索向量
ALTER TABLE ideas ADD COLUMN ts_content tsvector;

-- 2. 创建 GIN 索引以加速全文检索
CREATE INDEX idx_ideas_ts_content ON ideas USING GIN (ts_content);

-- 3. 创建函数：将 title 和 content 合并并转换为 tsvector
-- 使用 'simple' 配置以兼容多语言（包括中文基础分词需求）
CREATE OR REPLACE FUNCTION ideas_tsvector_trigger() RETURNS trigger AS $$
BEGIN
  new.ts_content :=
    to_tsvector('simple', coalesce(new.title, '')) ||
    to_tsvector('simple', coalesce(new.content, ''));
  RETURN new;
END
$$ LANGUAGE plpgsql;

-- 4. 创建触发器：在插入或更新时自动同步 ts_content
CREATE TRIGGER trg_ideas_upsert_tsvector
BEFORE INSERT OR UPDATE ON ideas
FOR EACH ROW EXECUTE FUNCTION ideas_tsvector_trigger();

-- 5. 初始化存量数据
UPDATE ideas SET ts_content = 
    to_tsvector('simple', coalesce(title, '')) || 
    to_tsvector('simple', coalesce(content, ''));
