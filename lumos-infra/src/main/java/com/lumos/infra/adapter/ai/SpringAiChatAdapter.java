package com.lumos.infra.adapter.ai;

import java.util.List;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.lumos.core.annotation.Auditable;
import com.lumos.core.port.out.ChatPort;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class SpringAiChatAdapter implements ChatPort {

    private final ChatClient chatClient;
    private final StreamingChatClient streamingChatClient;

    public SpringAiChatAdapter(@Qualifier("openAiChatClient") ChatClient chatClient, StreamingChatClient streamingChatClient) {
        this.chatClient = chatClient;
        this.streamingChatClient = streamingChatClient;
    }

    @Override
    @Auditable(operationType = "CHAT")
    public String chat(String systemPrompt, String userQuery) {
        Prompt prompt = createPrompt(systemPrompt, userQuery);
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

    @Override
    @Auditable(operationType = "CHAT")
    public Flux<String> streamChat(String systemPrompt, String userQuery) {
        Prompt prompt = createPrompt(systemPrompt, userQuery);
        return streamingChatClient.stream(prompt)
                .map(response -> {
                    String content = response.getResult().getOutput().getContent();
                    return content != null ? content : "";
                })
                .filter(content -> !content.isEmpty());
    }

    private Prompt createPrompt(String systemPrompt, String userQuery) {
        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(userQuery);

        // 默认开启数据库查询工具支持 (Text-to-SQL)
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withFunction("databaseQueryTool")
                .build();

        return new Prompt(List.of(systemMessage, userMessage), options);
    }
}
