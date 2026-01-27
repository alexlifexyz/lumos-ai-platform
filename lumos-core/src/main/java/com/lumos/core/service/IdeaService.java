package com.lumos.core.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Idea;
import com.lumos.core.port.out.IdeaRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdeaService {

    private final IdeaRepositoryPort ideaRepository;
    private final com.lumos.core.port.out.EmbeddingPort embeddingPort;
    private final com.lumos.core.port.out.VectorStorePort vectorStorePort;

    @Transactional
    public Idea createIdea(Idea idea) {
        if (idea.getUuid() == null) {
            idea.setUuid(UUID.randomUUID());
        }
        log.info("Creating new idea: {}", idea.getTitle());
        
        // 1. Persist Core Data
        Idea savedIdea = ideaRepository.save(idea);

        // 2. Generate Embedding (Async candidate, but sync for simplicity now)
        try {
            var vector = embeddingPort.embed(savedIdea.getContent());
            
            // 3. Persist Vector
            vectorStorePort.saveVector(savedIdea.getId(), vector);
        } catch (Exception e) {
            log.error("Failed to generate/save vector for idea: {}", savedIdea.getId(), e);
            // We don't rollback the Idea creation just because vector failed, 
            // but in strict RAG apps, maybe we should. For now, log and continue.
        }

        return savedIdea;
    }

    @Transactional(readOnly = true)
    public Idea getIdea(UUID uuid) {
        return ideaRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + uuid));
    }
}
