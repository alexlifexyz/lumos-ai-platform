package com.lumos.infra.adapter.ai;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.ChatPort;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("local")
@Slf4j
public class MockChatAdapter implements ChatPort {

    @Override
    public String chat(String systemPrompt, String userQuery) {
        log.info("[MOCK CHAT] System: {}\nUser:જી", systemPrompt, userQuery);
        // 如果是意图识别，返回简单的结果
        if (userQuery.contains("意图")) {
            return "GENERAL"; // 默认返回通用意图，避免路由失败
        }
        return "这是一个本地模拟的 AI 回复。您的请求是：" + userQuery;
    }

    @Override
    public Flux<String> streamChat(String systemPrompt, String userQuery) {
        log.info("[MOCK STREAM CHAT] System: {}\nUser:જી", systemPrompt, userQuery);
        return Flux.just("这是", "一个", "本地", "模拟", "的", "流式", "回复", "。");
    }
}
