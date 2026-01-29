package com.lumos.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Idea;
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.core.port.out.VectorStorePort;
import com.lumos.core.port.out.RerankPort;

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

    @Transactional(readOnly = true)
    public List<Idea> search(String query, int limit) {
        log.info("Searching for query: {}", query);

        // 1. 生成查询向量
        List<Double> queryVector = embeddingPort.embed(query);

        // 2. 混合检索召回 (通常召回比 limit 更多的候选，以便重排序)
        int recallLimit = Math.max(limit * 4, 20); // 至少召回 20 条，或 limit 的 4 倍
        List<Long> ideaIds = vectorStorePort.searchHybrid(queryVector, query, recallLimit);
        
        log.info("Recall found {} candidates", ideaIds.size());
        if (ideaIds.isEmpty()) {
            return List.of();
        }

        // 3. 获取实体详情并保留召回顺序
        List<Idea> candidates = ideaRepository.findAllByIds(ideaIds);

        // 4. 重排序 (Reranking)
        List<Idea> reranked = rerankPort.rerank(query, candidates);

        // 5. 返回最终 limit 数量的结果
        return reranked.stream()
                .limit(limit)
                .toList();
    }
}
