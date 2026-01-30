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
     * 全域集成搜索 (Idea + Document Chunks) - 默认 Namespace
     */
    @Transactional(readOnly = true)
    public List<SearchResult> search(String query, int limit) {
        return search(query, limit, "default");
    }

    /**
     * 全域集成搜索 (Idea + Document Chunks) - 指定 Namespace
     */
    @Transactional(readOnly = true)
    public List<SearchResult> search(String query, int limit, String namespace) {
        log.info("Searching across knowledge bases for query: '{}' in namespace: '{}'", query, namespace);

        // 1. 生成查询向量
        List<Double> queryVector = embeddingPort.embed(query);

        // 2. 双路召回 (Idea & Chunks)
        int recallLimit = Math.max(limit * 4, 20);
        
        // TODO: Idea 暂时不支持 Namespace 过滤，或者全量召回
        List<Long> ideaIds = vectorStorePort.searchHybrid(queryVector, query, recallLimit);
        
        // Chunks 支持 Namespace 过滤
        List<Long> chunkIds = chunkVectorStore.searchChunksHybrid(queryVector, query, recallLimit, namespace);

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

        chunks.forEach(c -> {
            // 注入扩展所需的元数据
            if (c.getMetadata() == null) {
                c.setMetadata(new java.util.HashMap<>());
            }
            c.getMetadata().put("internal_doc_id", c.getDocumentId());
            c.getMetadata().put("chunk_index", c.getChunkIndex());
            
            allCandidates.add(SearchResult.builder()
                .content(c.getContent())
                .sourceType(SearchResult.SourceType.DOCUMENT)
                .sourceName(c.getDocumentName())
                .sourceId(c.getDocumentUuid())
                .metadata(c.getMetadata())
                .build());
        });

        // 5. 全局重排序 (跨源打分)
        List<SearchResult> reranked = rerankPort.rerank(query, allCandidates);
        
        log.info("Reranking completed. Final candidate count: {}", reranked.size());
        
        List<SearchResult> topResults = reranked.stream()
                .limit(limit)
                .toList();

        // 6. 上下文窗口扩展 (Phase 7.1)
        return expandContext(topResults);
    }

    /**
     * 对文档类型的搜索结果进行上下文扩展 (Window Retrieval)
     */
    private List<SearchResult> expandContext(List<SearchResult> results) {
        // 简单实现：逐个扩展 (未来可优化为批量合并查询)
        int windowSize = 1; 

        return results.stream().map(result -> {
            if (result.getSourceType() != SearchResult.SourceType.DOCUMENT) {
                return result;
            }

            try {
                // 从 metadata 提取关键信息 (假设在构建 SearchResult 时已放入)
                Long docId = (Long) result.getMetadata().get("internal_doc_id");
                Integer index = (Integer) result.getMetadata().get("chunk_index");

                if (docId == null || index == null) {
                    return result;
                }

                // 计算窗口
                int start = Math.max(0, index - windowSize);
                int end = index + windowSize;

                // 查询范围内的 Chunks
                List<Chunk> windowChunks = documentRepository.findChunksByDocumentIdAndIndexRange(docId, start, end);
                
                // 拼接内容
                String expandedContent = windowChunks.stream()
                        .map(Chunk::getContent)
                        .reduce((a, b) -> a + "\n...\n" + b)
                        .orElse(result.getContent());
                
                // 更新结果 (创建新对象避免副作用)
                result.setContent(expandedContent);
                result.getMetadata().put("is_expanded", true);
                result.getMetadata().put("expanded_chunks", windowChunks.size());
                
                return result;

            } catch (Exception e) {
                log.warn("Failed to expand context for result: {}", result.getSourceName(), e);
                return result;
            }
        }).toList();
    }
}
