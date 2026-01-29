package com.lumos.web.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.lumos.core.service.AgenticService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(classes = com.lumos.web.LumosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("local")
@org.springframework.test.context.TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
class AgentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AgenticService agenticService;

    @MockBean
    private com.lumos.infra.persistence.repository.IdeaRepository ideaRepository;

    @MockBean
    private org.springframework.ai.chat.ChatClient chatClient;

    @MockBean
    private org.springframework.ai.chat.StreamingChatClient streamingChatClient;

    @MockBean
    private org.springframework.ai.embedding.EmbeddingClient embeddingClient;

    @MockBean
    private com.lumos.core.port.out.AuditPort auditPort;

    @Test
    void streamChat_ShouldReturnEvents() {
        // Arrange
        String query = "Hello";
        Flux<String> mockResponse = Flux.just("Hello", "world", "!");
        when(agenticService.executeStreamingQuery(anyString())).thenReturn(mockResponse);

        // Act
        Flux<String> resultFlux = webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/agent/chat/stream")
                        .queryParam("query", query)
                        .build())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody();

        // Assert
        StepVerifier.create(resultFlux)
                .expectNext("Hello")
                .expectNext("world")
                .expectNext("!")
                .verifyComplete();
    }
}
