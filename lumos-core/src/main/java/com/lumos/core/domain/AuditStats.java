package com.lumos.core.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditStats {
    private long totalRequests;
    private long totalTokens;
    private double averageDurationMs;
}
