package com.lumos.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@org.springframework.test.context.TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
public class IdeaFlowIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.chat.ChatClient chatClient;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.chat.StreamingChatClient streamingChatClient;
    
    @org.springframework.boot.test.mock.mockito.MockBean
    private org.springframework.ai.embedding.EmbeddingClient embeddingClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAndGetAndSearchFlow() throws Exception {
        // 1. Create Idea
        String json = """
            {
                "title": "Integration Test",
                "content": "Testing from Controller to DB",
                "tags": ["test"],
                "metadata": {"key": "val"}
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/ideas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Integration Test"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String uuid = objectMapper.readTree(response).get("uuid").asText();

        // 2. Get Idea
        mockMvc.perform(get("/api/v1/ideas/" + uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Testing from Controller to DB"));

        // 3. Search Idea (Should trigger Mock AI)
        mockMvc.perform(get("/api/v1/ideas/search?query=test"))
                .andExpect(status().isOk());
    }
}
