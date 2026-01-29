package com.lumos.infra.adapter.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import com.lumos.core.domain.Idea;

@RestClientTest(HttpRerankAdapter.class)
@org.springframework.context.annotation.Import(HttpRerankAdapter.class)
@TestPropertySource(properties = {
    "lumos.ai.rerank.enabled=true",
    "lumos.ai.rerank.api-key=test-key"
})
class HttpRerankAdapterTest {

    @org.springframework.boot.test.context.TestConfiguration
    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    static class TestConfig {}

    @Autowired
    private HttpRerankAdapter rerankAdapter;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void rerank_ShouldReturnReorderedListBasedOnScores() {
        // Arrange
        String query = "AI search";
        List<Idea> candidates = List.of(
            Idea.builder().id(1L).title("Doc 1").content("Content 1").build(),
            Idea.builder().id(2L).title("Doc 2").content("Content 2").build()
        );

        // 模拟 Jina AI 响应：假设 Doc 2 (index 1) 分数更高
        String mockResponse = """
            {
                "results": [
                    {"index": 1, "relevance_score": 0.95},
                    {"index": 0, "relevance_score": 0.45}
                ]
            }
            """;

        server.expect(requestTo("https://api.jina.ai/v1/rerank"))
              .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // Act
        List<Idea> result = rerankAdapter.rerank(query, candidates);

        // Assert
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId(), "Doc 2 should be first");
        assertEquals(1L, result.get(1).getId(), "Doc 1 should be second");
    }
}
