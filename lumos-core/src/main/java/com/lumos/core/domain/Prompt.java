package com.lumos.core.domain;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prompt {
    private Long id;
    private String code;        // Prompt 唯一编码 (如: DEFAULT_AGENT, RAG_ANSWER)
    private String name;        // 友好名称
    private String content;     // Prompt 内容模板
    private String description; // 描述
    private Instant createdAt;
    private Instant updatedAt;
}
