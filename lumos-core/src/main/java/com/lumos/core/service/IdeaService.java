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

    @Transactional
    public Idea createIdea(Idea idea) {
        if (idea.getUuid() == null) {
            idea.setUuid(UUID.randomUUID());
        }
        log.info("Creating new idea: {}", idea.getTitle());
        // TODO: In Day 3, we will add Embedding generation here
        return ideaRepository.save(idea);
    }

    @Transactional(readOnly = true)
    public Idea getIdea(UUID uuid) {
        return ideaRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Idea not found: " + uuid));
    }
}
