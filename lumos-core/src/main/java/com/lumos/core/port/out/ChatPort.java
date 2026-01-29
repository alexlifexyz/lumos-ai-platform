package com.lumos.core.port.out;

import reactor.core.publisher.Flux;

/**
 * AI 聊天接口：支持阻塞和流式响应
 */
public interface ChatPort {
    /**
     * 同步聊天查询
     */
    String chat(String systemPrompt, String userQuery);

    /**
     * 流式聊天查询
     */
    Flux<String> streamChat(String systemPrompt, String userQuery);
}
