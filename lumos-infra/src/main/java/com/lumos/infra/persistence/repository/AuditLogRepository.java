package com.lumos.infra.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lumos.infra.persistence.entity.AuditLogEntity;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    
    @Query("SELECT COUNT(a) FROM AuditLogEntity a")
    long countTotalRequests();

    @Query("SELECT SUM(a.totalTokens) FROM AuditLogEntity a")
    Long sumTotalTokens();

    @Query("SELECT AVG(a.durationMs) FROM AuditLogEntity a")
    Double avgDuration();
}
