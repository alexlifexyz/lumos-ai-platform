package com.lumos.infra.adapter.ai;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.lumos.core.domain.SearchResult;
import com.lumos.core.port.out.RerankPort;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于 HTTP 的重排序适配器 (兼容 Jina AI / BGE Reranker 格式)
 */
@Component
@ConditionalOnProperty(name = "lumos.ai.rerank.enabled", havingValue = "true")
@Slf4j
public class HttpRerankAdapter implements RerankPort {

    private final RestClient restClient;
    private final String model;

    public HttpRerankAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${lumos.ai.rerank.base-url:https://api.jina.ai/v1/rerank}") String baseUrl,
            @Value("${lumos.ai.rerank.api-key:}") String apiKey,
            @Value("${lumos.ai.rerank.model:rerank-multilingual-v2.0}") String model) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.model = model;
    }

    @Override
    public List<SearchResult> rerank(String query, List<SearchResult> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        log.info("Reranking {} candidates using model: {}", candidates.size(), model);

        List<String> documents = candidates.stream()
                .map(i -> i.getSourceName() + " " + i.getContent())
                .toList();

        RerankRequest request = new RerankRequest(model, query, documents);

        try {
            RerankResponse response = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(RerankResponse.class);

            if (response == null || response.getResults() == null) {
                log.warn("Rerank API returned empty results, falling back to original order.");
                return candidates;
            }

            // 根据返回的 score 重新排序
            return response.getResults().stream()
                    .sorted(Comparator.comparingDouble(RerankResult::getRelevanceScore).reversed())
                    .map(r -> candidates.get(r.getIndex()))
                    .toList();

        } catch (Exception e) {
            log.error("Rerank API call failed: {}. Falling back to original order.", e.getMessage());
            return candidates;
        }
    }

    @Data
    private static class RerankRequest {
        private final String model;
        private final String query;
        private final List<String> documents;
        private final int top_n;

        public RerankRequest(String model, String query, List<String> documents) {
            this.model = model;
            this.query = query;
            this.documents = documents;
            this.top_n = documents.size();
        }
    }

    @Data
    private static class RerankResponse {
        private List<RerankResult> results;
    }

    @Data
    private static class RerankResult {
        private int index;
        private double relevanceScore; // 对应 JSON 中的 relevance_score
        
        // 处理不同 API 字段名差异 (Jina 使用 relevance_score)
        public void setRelevance_score(double score) {
            this.relevanceScore = score;
        }
    }
}
