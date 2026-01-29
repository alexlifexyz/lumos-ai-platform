package com.lumos.web.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import com.lumos.core.domain.Idea;
import com.lumos.core.service.IdeaService;
import com.lumos.core.service.SearchService;
import com.lumos.core.service.AgenticService;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = com.lumos.web.LumosApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@org.springframework.test.context.TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
class IdeaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdeaService ideaService;

    @MockBean
    private SearchService searchService;

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
    void search_ShouldReturnIdeas() throws Exception {
        // Arrange
        String query = "test";
        com.lumos.core.domain.SearchResult mockResult = com.lumos.core.domain.SearchResult.builder()
                .content("Result Content")
                .sourceName("Source Name")
                .sourceType(com.lumos.core.domain.SearchResult.SourceType.IDEA)
                .build();
        
        when(searchService.search(eq(query), anyInt())).thenReturn(java.util.List.of(mockResult));

        // Act & Assert
        mockMvc.perform(get("/api/v1/ideas/search")
                .param("query", query)
                .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Result Content"))
                .andExpect(jsonPath("$[0].sourceName").value("Source Name"));
    }
}
