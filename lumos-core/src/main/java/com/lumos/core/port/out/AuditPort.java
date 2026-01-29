package com.lumos.core.port.out;

import java.util.List;

import com.lumos.core.domain.AuditLog;
import com.lumos.core.domain.AuditStats;

public interface AuditPort {
    void save(AuditLog auditLog);
    List<AuditLog> findAll(int limit);
    AuditStats getGlobalStats();
}
