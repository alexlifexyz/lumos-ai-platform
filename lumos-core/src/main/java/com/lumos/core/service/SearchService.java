package com.lumos.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Idea;
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.core.port.out.VectorStorePort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final IdeaRepositoryPort ideaRepository;

    @Transactional(readOnly = true)
    public List<Idea> search(String query, int limit) {
        log.info("Searching for query: {}", query);

        // 1. Embed the query
        List<Double> queryVector = embeddingPort.embed(query);

        // 2. Hybrid Search (Vector + Keyword)
        List<Long> ideaIds = vectorStorePort.searchHybrid(queryVector, query, limit);
        
        log.info("Search found {} candidates", ideaIds.size());

        // 3. Hydrate content (Fetch details from DB)
        // In a real system, we might want to preserve the order returned by vector store.
        // JPA's findAllById doesn't guarantee order.
        return ideaRepository.findAllByIds(ideaIds);
    }
}
