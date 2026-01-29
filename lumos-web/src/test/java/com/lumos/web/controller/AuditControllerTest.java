package com.lumos.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.lumos.core.domain.AuditStats;
import com.lumos.core.port.out.AuditPort;
import com.lumos.core.service.AgenticService;
import com.lumos.core.service.IdeaService;
import com.lumos.core.service.SearchService;

@SpringBootTest(classes = com.lumos.web.LumosApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("local")
@TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditPort auditPort;

    // Mock 其他 Controller 可能触发初始化的依赖
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

    @Test
    void getGlobalStats_ShouldReturnStats() throws Exception {
        // Arrange
        AuditStats mockStats = AuditStats.builder()
                .totalRequests(100)
                .totalTokens(5000L)
                .averageDurationMs(250.5)
                .build();
        
        when(auditPort.getGlobalStats()).thenReturn(mockStats);

        // Act & Assert
        mockMvc.perform(get("/api/v1/admin/audit/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").value(100))
                .andExpect(jsonPath("$.totalTokens").value(5000))
                .andExpect(jsonPath("$.averageDurationMs").value(250.5));
    }
}
