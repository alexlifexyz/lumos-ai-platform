package com.lumos.infra.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@Slf4j
public class LumosAiConfiguration {

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Bean
    public OpenAiApi openAiApi() {
        String fixedUrl = baseUrl;
        
        // 核心逻辑：如果 URL 以 /v1 结尾，则移除它
        // 因为 Spring AI 的 OpenAiApi 会自动在 baseUrl 后面拼接 /v1
        if (fixedUrl != null && fixedUrl.endsWith("/v1")) {
            fixedUrl = fixedUrl.substring(0, fixedUrl.length() - 3);
            log.info("Detected redundant '/v1' in Base URL, automatically fixed to: {}", fixedUrl);
        } else if (fixedUrl != null && fixedUrl.endsWith("/v1/")) {
            fixedUrl = fixedUrl.substring(0, fixedUrl.length() - 4);
            log.info("Detected redundant '/v1/' in Base URL, automatically fixed to: {}", fixedUrl);
        }

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("OpenAI API Key is missing! AI features will not work.");
        }

        return new OpenAiApi(fixedUrl, apiKey);
    }
}
