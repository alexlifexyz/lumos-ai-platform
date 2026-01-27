package com.lumos.infra.adapter.persistence;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.VectorStorePort;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("local")
@Slf4j
public class NoOpVectorStoreAdapter implements VectorStorePort {

    @Override
    public void saveVector(Long ideaId, List<Double> vector) {
        log.info("[MOCK] Skipping vector persistence for Idea ID: {} (H2 mode)", ideaId);
    }

    @Override
    public List<Long> searchVectors(List<Double> queryVector, int limit) {
        log.info("[MOCK] Searching vectors (H2 mode). Returning empty list.");
        return List.of();
    }
}