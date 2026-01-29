package com.lumos.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Chunk;
import com.lumos.core.domain.Idea;
import com.lumos.core.domain.SearchResult;
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.core.port.out.VectorStorePort;
import com.lumos.core.port.out.RerankPort;
import com.lumos.core.port.out.ChunkVectorStorePort;
import com.lumos.core.port.out.DocumentRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final EmbeddingPort embeddingPort;
    private final VectorStorePort vectorStorePort;
    private final IdeaRepositoryPort ideaRepository;
    private final RerankPort rerankPort;
    private final ChunkVectorStorePort chunkVectorStore;
    private final DocumentRepositoryPort documentRepository;

    /**
     * 全域集成搜索 (Idea + Document Chunks)
     */
    @Transactional(readOnly = true)
    public List<SearchResult> search(String query, int limit) {
        log.info("Searching across all knowledge bases for query: {}", query);

        // 1. 生成查询向量
        List<Double> queryVector = embeddingPort.embed(query);

        // 2. 双路召回 (Idea & Chunks)
        int recallLimit = Math.max(limit * 4, 20);
        
        List<Long> ideaIds = vectorStorePort.searchHybrid(queryVector, query, recallLimit);
        List<Long> chunkIds = chunkVectorStore.searchChunksHybrid(queryVector, query, recallLimit);

        // 3. 回填实体详情
        List<Idea> ideas = ideaRepository.findAllByIds(ideaIds);
        List<Chunk> chunks = documentRepository.findAllChunksByIds(chunkIds);

        // 4. 转换为统一搜索结果
        List<SearchResult> allCandidates = new ArrayList<>();
        
        ideas.forEach(i -> allCandidates.add(SearchResult.builder()
                .content(i.getContent())
                .sourceType(SearchResult.SourceType.IDEA)
                .sourceName(i.getTitle())
                .sourceId(i.getUuid().toString())
                .metadata(i.getMetadata())
                .build()));

        chunks.forEach(c -> allCandidates.add(SearchResult.builder()
                .content(c.getContent())
                .sourceType(SearchResult.SourceType.DOCUMENT)
                .sourceName(c.getDocumentName())
                .sourceId(c.getDocumentUuid())
                .metadata(c.getMetadata())
                .build()));

        // 5. 全局重排序 (跨源打分)
        List<SearchResult> reranked = rerankPort.rerank(query, allCandidates);
        
        log.info("Reranking completed. Final candidate count: {}", reranked.size());
        
        return reranked.stream()
                .limit(limit)
                .toList();
    }
}
