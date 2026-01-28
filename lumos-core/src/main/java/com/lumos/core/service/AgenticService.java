package com.lumos.core.service;

import java.util.List;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import com.lumos.api.dto.AgentQueryResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AgenticService {

    private final ChatClient chatClient;

    public AgenticService(@org.springframework.beans.factory.annotation.Autowired(required = false) ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    private static final String SYSTEM_PROMPT = """
        你是一个 Lumos 平台的 Text-to-SQL 专家。
        你可以通过调用 `databaseQueryTool` 函数来查询数据库以回答用户问题。
        
        数据库 Schema 如下：
        - ideas 表: 存储知识点
            - id: 主键
            - title: 标题
            - content: 内容
            - tags: 标签数组
            - metadata: JSONB 格式的元数据
            - created_at: 创建时间
        - audit_logs 表: 存储 AI 调用审计日志
            - id: 主键
            - operation_type: 操作类型 (如 'CHAT', 'EMBEDDING')
            - model_name: 使用的模型名称
            - total_tokens: 消耗的总 Token 数
            - duration_ms: 耗时（毫秒）
            - created_at: 创建时间
            
        回答要求：
        1. 优先通过 SQL 查询获取事实。
        2. 如果查询结果为空，如实告知。
        3. 最终回复应使用中文，且语气专业。
        4. 不要向用户解释你正在调用函数，直接给出基于查询结果的回答。
        """;

    public AgentQueryResponse executeQuery(String userQuery) {
        log.info("Agent processing query: {}", userQuery);

        if (chatClient == null) {
            log.warn("ChatClient is not configured. Returning mock response.");
            return new AgentQueryResponse("AI 服务未配置，无法执行智能查询。", null, null);
        }

        SystemMessage systemMessage = new SystemMessage(SYSTEM_PROMPT);
        UserMessage userMessage = new UserMessage(userQuery);

        // 配置 Function Calling
        // Spring AI 会自动根据名称在 Spring Context 中寻找对应的 Function Bean
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withFunction("databaseQueryTool")
                .build();

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage), options);
        
        try {
            ChatResponse response = chatClient.call(prompt);
            String answer = response.getResult().getOutput().getContent();
            return new AgentQueryResponse(answer, null, null);
        } catch (Exception e) {
            log.error("Agent query failed", e);
            return new AgentQueryResponse("查询执行失败: " + e.getMessage(), null, null);
        }
    }
}
