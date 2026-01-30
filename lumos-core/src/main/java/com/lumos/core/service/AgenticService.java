package com.lumos.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lumos.api.dto.AgentQueryResponse;
import com.lumos.core.domain.Intent;
import com.lumos.core.domain.SearchResult;
import com.lumos.core.port.out.ChatPort;
import com.lumos.core.port.out.GuardrailPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgenticService {

    private final ChatPort chatPort;
    private final PromptService promptService;
    private final GuardrailPort guardrailPort;
    private final IntentRouterService intentRouterService;
    private final SearchService searchService;

    private static final String DEFAULT_SYSTEM_PROMPT = """
        你是一个 Lumos 平台的 AI 助手。
        你可以回答用户的任何问题，语气应专业、友好且简洁。
        最终回复应使用中文。
        """;

    private static final String RAG_SYSTEM_PROMPT = """
        你是一个基于知识库的问答助手。
        请根据以下提供的【背景知识】来回答用户的问题。
        如果背景知识中没有相关信息，请委婉地告知用户你不知道，不要胡乱编造。
        
        【背景知识】
        {{CONTEXT}}
        
        最终回复应使用中文。
        """;

    /**
     * 同步查询
     */
    public AgentQueryResponse executeQuery(String userQuery) {
        log.info("Agent processing synchronous query: {}", userQuery);

        // 1. 输入安全检查
        if (guardrailPort.isToxic(userQuery)) {
            return new AgentQueryResponse("抱歉，您的请求包含敏感内容，无法处理。", null, null);
        }

        // 2. 意图识别
        Intent intent = intentRouterService.route(userQuery);
        log.info("Routed intent: {}", intent);

        String systemPrompt;
        String finalUserQuery = userQuery;

        if (intent == Intent.RAG) {
            // 执行 RAG 流程
            List<SearchResult> results = searchService.search(userQuery, 5);
            String context = results.stream()
                    .map(r -> "[" + r.getSourceName() + "]: " + r.getContent())
                    .collect(Collectors.joining("\n\n"));
            
            systemPrompt = promptService.getPromptContent("RAG_AGENT", RAG_SYSTEM_PROMPT)
                    .replace("{{CONTEXT}}", context);
        } else if (intent == Intent.SQL) {
            // 暂时模拟 SQL 路由，未来集成 SQL Agent
            return new AgentQueryResponse("检测到您想查询统计数据，该功能（SQL Agent）正在开发中...", null, null);
        } else {
            systemPrompt = promptService.getPromptContent("DEFAULT_AGENT", DEFAULT_SYSTEM_PROMPT);
        }

        try {
            // 3. 执行 AI 查询
            String answer = chatPort.chat(systemPrompt, finalUserQuery);

            // 4. 输出内容脱敏
            String sanitizedAnswer = guardrailPort.sanitize(answer);
            
            return new AgentQueryResponse(sanitizedAnswer, null, null);
        } catch (Exception e) {
            log.error("Agent query failed", e);
            return new AgentQueryResponse("查询执行失败: " + e.getMessage(), null, null);
        }
    }

    /**
     * 流式查询
     */
    public Flux<String> executeStreamingQuery(String userQuery) {
        log.info("Agent processing streaming query: {}", userQuery);

        if (guardrailPort.isToxic(userQuery)) {
            return Flux.just("抱歉，您的请求包含敏感内容，无法处理。");
        }

        Intent intent = intentRouterService.route(userQuery);
        
        String systemPrompt;
        if (intent == Intent.RAG) {
            List<SearchResult> results = searchService.search(userQuery, 3);
            String context = results.stream()
                    .map(r -> "[" + r.getSourceName() + "]: " + r.getContent())
                    .collect(Collectors.joining("\n"));
            systemPrompt = promptService.getPromptContent("RAG_AGENT", RAG_SYSTEM_PROMPT)
                    .replace("{{CONTEXT}}", context);
        } else {
            systemPrompt = promptService.getPromptContent("DEFAULT_AGENT", DEFAULT_SYSTEM_PROMPT);
        }
        
        return chatPort.streamChat(systemPrompt, userQuery)
                .doOnError(e -> log.error("Streaming query failed", e));
    }
}
