package com.lumos.core.service;

import org.springframework.stereotype.Service;

import com.lumos.core.domain.Intent;
import com.lumos.core.port.out.ChatPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntentRouterService {

    private final ChatPort chatPort;
    private final PromptService promptService;

    private static final String ROUTER_PROMPT = """
        你是一个意图识别专家。
        你的任务是分析用户的输入，并将其归类为以下三种意图之一：
        1. SQL: 当用户询问统计数据、数量、最近创建的项目等可以通过数据库 SQL 查询获取的信息时。
        2. RAG: 当用户询问具体的知识点、文档内容、方案细节等需要从知识库中检索的信息时。
        3. GENERAL: 当用户进行普通打招呼、闲聊或以上两者都不符合时。

        请仅输出意图代码（SQL, RAG 或 GENERAL），不要输出任何其他文字。

        用户输入: "{{QUERY}}"
        """;

    public Intent route(String userQuery) {
        String systemPrompt = promptService.getPromptContent("INTENT_ROUTER", "你是一个意图识别专家。请仅返回意图代码。");
        String finalPrompt = ROUTER_PROMPT.replace("{{QUERY}}", userQuery);

        try {
            String result = chatPort.chat(systemPrompt, finalPrompt).trim().toUpperCase();
            log.info("Detected intent for query '{}': {}", userQuery, result);
            
            if (result.contains("SQL")) return Intent.SQL;
            if (result.contains("RAG")) return Intent.RAG;
            return Intent.GENERAL;
        } catch (Exception e) {
            log.error("Intent routing failed, falling back to GENERAL", e);
            return Intent.GENERAL;
        }
    }
}
