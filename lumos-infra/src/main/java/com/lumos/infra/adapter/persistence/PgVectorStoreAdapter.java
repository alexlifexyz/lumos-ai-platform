package com.lumos.infra.adapter.persistence;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.VectorStorePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!local") // 仅在有 Postgres 环境下激活
@RequiredArgsConstructor
@Slf4j
public class PgVectorStoreAdapter implements VectorStorePort {

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
        String sql = """
            SELECT i.id FROM ideas i
            LEFT JOIN idea_vectors v ON i.id = v.idea_id
            ORDER BY (
                0.7 * (1 - COALESCE(v.embedding <=> :queryVector::vector, 1)) + 
                0.3 * ts_rank(to_tsvector('english', i.content || ' ' || i.title), plainto_tsquery('english', :keyword))
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
