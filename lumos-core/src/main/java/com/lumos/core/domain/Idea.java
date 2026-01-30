package com.lumos.core.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Idea {
    private Long id;
    private UUID uuid;
    private String title;
    private String content;
    private List<String> tags;     // 标签
    private Map<String, Object> metadata; // 扩展元数据
    
    @Builder.Default
    private String namespace = "default"; // 知识库隔离命名空间

    private Instant createdAt;
    private Instant updatedAt;

    // Domain Logic methods can go here
    public void updateContent(String newTitle, String newContent) {
        this.title = newTitle;
        this.content = newContent;
        this.updatedAt = Instant.now();
    }
}
