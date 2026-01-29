package com.lumos.core.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditLog {
    private Long id;
    private String traceId;       // 链路追踪 ID，关联一次完整的业务请求
    private String operationType; // 操作类型: CHAT, EMBEDDING, RERANK
    private String modelName;     // 模型名称
    private Integer promptTokens; // 提示词 Token 数
    private Integer completionTokens; // 生成 Token 数
    private Integer totalTokens;  // 总 Token 数
    private Long durationMs;      // 耗时 (毫秒)
    private Instant createdAt;    // 创建时间
}
