package com.lumos.core.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lumos.api.dto.AgentQueryResponse;
import com.lumos.core.port.out.ChatPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgenticService {

    private final ChatPort chatPort;

    private static final String SYSTEM_PROMPT = """
        你是一个 Lumos 平台的 AI 助手。
        你可以回答用户的任何问题，语气应专业、友好且简洁。
        最终回复应使用中文。
        """;

    /**
     * 同步查询
     */
    public AgentQueryResponse executeQuery(String userQuery) {
        log.info("Agent processing synchronous query: {}", userQuery);
        try {
            String answer = chatPort.chat(SYSTEM_PROMPT, userQuery);
            return new AgentQueryResponse(answer, null, null);
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
        return chatPort.streamChat(SYSTEM_PROMPT, userQuery)
                .doOnError(e -> log.error("Streaming query failed", e));
    }
}
