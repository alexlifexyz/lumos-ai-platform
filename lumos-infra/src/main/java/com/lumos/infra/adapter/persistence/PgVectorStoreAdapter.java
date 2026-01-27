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
        // Convert List<Double> to String format "[0.1,0.2,...]" compatible with pgvector
        String vectorStr = vector.toString(); 
        
        // Use native SQL insert
        String sql = "INSERT INTO idea_vectors (idea_id, embedding, model_version) VALUES (:id, :embedding::vector, :version) " +
                     "ON CONFLICT (idea_id) DO UPDATE SET embedding = EXCLUDED.embedding";

        jdbcClient.sql(sql)
                .param("id", ideaId)
                .param("embedding", vectorStr)
                .param("version", "text-embedding-3-small") // Hardcoded for Phase 1
                .update();
        
        log.info("Saved vector for Idea ID: {}", ideaId);
    }

    @Override
    public List<Long> searchVectors(List<Double> queryVector, int limit) {
        String vectorStr = queryVector.toString();

        // Cosine distance search: order by embedding <=> query_vector
        String sql = """
            SELECT idea_id FROM idea_vectors 
            ORDER BY embedding <=> :queryVector::vector 
            LIMIT :limit
        """;

        return jdbcClient.sql(sql)
                .param("queryVector", vectorStr)
                .param("limit", limit)
                .query(Long.class)
                .list();
    }
}