package com.lumos.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lumos.core.domain.AuditLog;
import com.lumos.core.domain.AuditStats;
import com.lumos.core.port.out.AuditPort;
import com.lumos.infra.persistence.entity.AuditLogEntity;
import com.lumos.infra.persistence.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JpaAuditAdapter implements AuditPort {

    private final AuditLogRepository repository;

    @Override
    public void save(AuditLog log) {
        AuditLogEntity entity = AuditLogEntity.builder()
                .traceId(log.getTraceId())
                .operationType(log.getOperationType())
                .modelName(log.getModelName())
                .promptTokens(log.getPromptTokens())
                .completionTokens(log.getCompletionTokens())
                .totalTokens(log.getTotalTokens())
                .durationMs(log.getDurationMs())
                .createdAt(log.getCreatedAt())
                .build();
        
        repository.save(entity);
    }

    @Override
    public List<AuditLog> findAll(int limit) {
        // Simple implementation for now, limit logic can be added via PageRequest
        return repository.findAll().stream()
                .limit(limit)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public AuditStats getGlobalStats() {
        long totalRequests = repository.countTotalRequests();
        Long totalTokens = repository.sumTotalTokens();
        Double avgDuration = repository.avgDuration();

        return AuditStats.builder()
                .totalRequests(totalRequests)
                .totalTokens(totalTokens != null ? totalTokens : 0)
                .averageDurationMs(avgDuration != null ? avgDuration : 0.0)
                .build();
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return AuditLog.builder()
                .id(entity.getId())
                .traceId(entity.getTraceId())
                .operationType(entity.getOperationType())
                .modelName(entity.getModelName())
                .promptTokens(entity.getPromptTokens())
                .completionTokens(entity.getCompletionTokens())
                .totalTokens(entity.getTotalTokens())
                .durationMs(entity.getDurationMs())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
