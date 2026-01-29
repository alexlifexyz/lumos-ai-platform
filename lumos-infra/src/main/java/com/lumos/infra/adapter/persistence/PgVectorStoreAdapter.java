package com.lumos.infra.adapter.persistence;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.ChunkVectorStorePort;
import com.lumos.core.port.out.VectorStorePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!local") // 仅在有 Postgres 环境下激活
@RequiredArgsConstructor
@Slf4j
public class PgVectorStoreAdapter implements VectorStorePort, ChunkVectorStorePort {

    private final JdbcClient jdbcClient;

    @Override
    public void saveVector(Long ideaId, List<Double> vector) {
        String vectorStr = vector.toString(); 
        String sql = "INSERT INTO idea_vectors (idea_id, embedding, model_version) VALUES (:id, :embedding::vector, :version) " +
                     "ON CONFLICT (idea_id) DO UPDATE SET embedding = EXCLUDED.embedding";

        jdbcClient.sql(sql)
                .param("id", ideaId)
                .param("embedding", vectorStr)
                .param("version", "text-embedding-v1") 
                .update();
        log.info("Saved vector for Idea ID: {}", ideaId);
    }

    @Override
    public void saveChunkVector(Long chunkId, List<Double> vector) {
        String vectorStr = vector.toString();
        String sql = "INSERT INTO chunk_vectors (chunk_id, embedding, model_version) VALUES (:id, :embedding::vector, :version) " +
                     "ON CONFLICT (chunk_id) DO UPDATE SET embedding = EXCLUDED.embedding";

        jdbcClient.sql(sql)
                .param("id", chunkId)
                .param("embedding", vectorStr)
                .param("version", "text-embedding-v1")
                .update();
        log.info("Saved vector for Chunk ID: {}", chunkId);
    }

    @Override
    public List<Long> searchChunkVectors(List<Double> queryVector, int limit) {
        String vectorStr = queryVector.toString();
        String sql = "SELECT chunk_id FROM chunk_vectors ORDER BY embedding <=> :queryVector::vector LIMIT :limit";
        return jdbcClient.sql(sql)
                .param("queryVector", vectorStr)
                .param("limit", limit)
                .query(Long.class)
                .list();
    }

    @Override
    public List<Long> searchChunksHybrid(List<Double> queryVector, String keyword, int limit) {
        String vectorStr = queryVector.toString();
        // 片段混合检索：向量权重 0.7，全文权重 0.3
        String sql = """
            SELECT c.id FROM document_chunks c
            LEFT JOIN chunk_vectors v ON c.id = v.chunk_id
            ORDER BY (
                0.7 * (1 - COALESCE(v.embedding <=> :queryVector::vector, 1)) + 
                0.3 * ts_rank(c.ts_content, plainto_tsquery('simple', :keyword))
            ) DESC
            LIMIT :limit
        """;

        return jdbcClient.sql(sql)
                .param("queryVector", vectorStr)
                .param("keyword", keyword)
                .param("limit", limit)
                .query(Long.class)
                .list();
    }

    @Override
    public List<Long> searchVectors(List<Double> queryVector, int limit) {
        String vectorStr = queryVector.toString();
        String sql = "SELECT idea_id FROM idea_vectors ORDER BY embedding <=> :queryVector::vector LIMIT :limit";
        return jdbcClient.sql(sql)
                .param("queryVector", vectorStr)
                .param("limit", limit)
                .query(Long.class)
                .list();
    }

    @Override
    public List<Long> searchHybrid(List<Double> queryVector, String keyword, int limit) {
        String vectorStr = queryVector.toString();
        // 混合检索逻辑：
        // 1. 向量相似度 (1 - Cosine Distance) 权重 0.7
        // 2. 全文检索排名 (ts_rank) 权重 0.3
        // 使用 plainto_tsquery 将用户输入转为检索词，配置使用 'simple'
        String sql = """
            SELECT i.id FROM ideas i
            LEFT JOIN idea_vectors v ON i.id = v.idea_id
            ORDER BY (
                0.7 * (1 - COALESCE(v.embedding <=> :queryVector::vector, 1)) + 
                0.3 * ts_rank(i.ts_content, plainto_tsquery('simple', :keyword))
            ) DESC
            LIMIT :limit
        """;

        return jdbcClient.sql(sql)
                .param("queryVector", vectorStr)
                .param("keyword", keyword)
                .param("limit", limit)
                .query(Long.class)
                .list();
    }
}
