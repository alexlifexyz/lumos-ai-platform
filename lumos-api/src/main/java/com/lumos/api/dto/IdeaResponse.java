package com.lumos.api.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record IdeaResponse(
    UUID uuid,
    String title,
    String content,
    List<String> tags,
    Map<String, Object> metadata,
    Instant createdAt,
    Instant updatedAt
) {}
