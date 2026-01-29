package com.lumos.api.dto;

import java.time.Instant;
import java.util.Map;

public record DocumentResponse(
    String uuid,
    String filename,
    String contentType,
    long size,
    String status,
    String failureReason,
    Map<String, Object> metadata,
    Instant createdAt,
    Instant updatedAt
) {}
