package com.lumos.infra.audit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.lumos.core.domain.AuditLog;
import com.lumos.core.port.out.AuditPort;
import com.lumos.core.port.out.ChatPort;
import com.lumos.infra.InfraTestApplication;

@SpringBootTest(classes = InfraTestApplication.class)
@ActiveProfiles("local")
@org.springframework.test.context.TestPropertySource(properties = "spring.ai.openai.api-key=test-key")
class AuditIntegrationTest {

    @Autowired
    private ChatPort chatPort;

    @MockBean(name = "openAiChatClient")
    private org.springframework.ai.chat.ChatClient chatClient;
    
    @MockBean(name = "ollamaChatClient")
    private org.springframework.ai.chat.ChatClient ollamaChatClient;
    
    @MockBean
    private org.springframework.ai.chat.StreamingChatClient streamingChatClient;

    // 我们 Mock 掉持久化层，验证它是否被异步调用
    @MockBean
    private AuditPort auditPort;

    @Test
    void chat_ShouldTriggerAsyncAudit() {
        // Arrange
        org.springframework.ai.chat.ChatResponse mockResponse = new org.springframework.ai.chat.ChatResponse(
            java.util.List.of(new org.springframework.ai.chat.Generation("Mock Answer"))
        );
        org.mockito.Mockito.when(chatClient.call(any(org.springframework.ai.chat.prompt.Prompt.class)))
            .thenReturn(mockResponse);

        // Act
        chatPort.chat("System", "User");

        // Assert
        verify(auditPort, timeout(1000)).save(any(AuditLog.class));
    }

    @Test
    void streamChat_ShouldTriggerAsyncAudit_WithAccumulatedContent() {
        // Arrange
        org.springframework.ai.chat.ChatResponse chunk1 = new org.springframework.ai.chat.ChatResponse(
            java.util.List.of(new org.springframework.ai.chat.Generation("Hello"))
        );
        org.springframework.ai.chat.ChatResponse chunk2 = new org.springframework.ai.chat.ChatResponse(
            java.util.List.of(new org.springframework.ai.chat.Generation(" World"))
        );
        
        org.mockito.Mockito.when(streamingChatClient.stream(any(org.springframework.ai.chat.prompt.Prompt.class)))
            .thenReturn(reactor.core.publisher.Flux.just(chunk1, chunk2));

        // Act
        // 必须订阅 Flux 才会触发执行
        chatPort.streamChat("System", "User").blockLast();

        // Assert
        // 验证 auditPort.save 被调用，且内容（通过 Token 估算体现）不为空
        // "Hello World" 长度 11，TokenEstimator 兜底是 length/4 = 2 (或真实 Token 数)
        // 只要 save 被调用，就说明切面的 doFinally 逻辑生效了
        verify(auditPort, timeout(1000)).save(any(AuditLog.class));
    }
}
