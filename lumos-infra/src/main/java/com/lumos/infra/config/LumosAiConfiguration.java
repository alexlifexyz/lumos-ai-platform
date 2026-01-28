package com.lumos.infra.config;

import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class LumosAiConfiguration {

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${app.ai.chat-model:gpt-3.5-turbo}")
    private String chatModel;

    @Value("${app.ai.embedding-model:text-embedding-3-small}")
    private String embeddingModel;

    @Bean
    @ConditionalOnExpression("!'${app.ai.api-key:}'.isEmpty()")
    public OpenAiApi openAiApi() {
        log.info("Initializing AI API with BaseURL: {}", baseUrl);
        return new OpenAiApi(baseUrl, apiKey);
    }

    @Bean
    @ConditionalOnBean(OpenAiApi.class)
    public OpenAiEmbeddingClient embeddingClient(OpenAiApi openAiApi) {
        log.info("Initializing Embedding Client with Model: {}", embeddingModel);
        return new OpenAiEmbeddingClient(openAiApi);
    }

    @Bean
    @ConditionalOnBean(OpenAiApi.class)
    public OpenAiChatClient chatClient(OpenAiApi openAiApi) {
        log.info("Initializing Chat Client with Model: {}", chatModel);
        return new OpenAiChatClient(openAiApi);
    }
}
