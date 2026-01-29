package com.lumos.infra.persistence.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String traceId;
    
    private String operationType;
    
    private String modelName;
    
    private Integer promptTokens;
    
    private Integer completionTokens;
    
    private Integer totalTokens;
    
    private Long durationMs;
    
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;
}
