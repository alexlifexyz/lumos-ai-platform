package com.lumos.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@org.springframework.test.context.TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
public class AgentIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.chat.ChatClient chatClient;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.chat.StreamingChatClient streamingChatClient;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAgentQueryMockResponse() throws Exception {
        String json = """
            {
                "query": "有多少个 Idea？"
            }
            """;

        mockMvc.perform(post("/api/v1/agent/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").exists());
    }
}
