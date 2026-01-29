package com.lumos.infra.adapter.ai;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.lumos.core.domain.SearchResult;
import com.lumos.core.port.out.RerankPort;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认重排序适配器：仅执行透传，不改变原始混合检索的顺序。
 */
@Component
@ConditionalOnProperty(name = "lumos.ai.rerank.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class DefaultRerankAdapter implements RerankPort {

    @Override
    public List<SearchResult> rerank(String query, List<SearchResult> candidates) {
        log.debug("Reranking is disabled. Passing through {} candidates.", candidates.size());
        return candidates;
    }
}
