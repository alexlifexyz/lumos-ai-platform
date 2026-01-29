package com.lumos.infra.audit;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.lumos.core.event.AuditEvent;
import com.lumos.core.port.out.AuditPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditPort auditPort;

    @Async
    @EventListener
    public void onAuditEvent(AuditEvent event) {
        try {
            log.debug("Processing async audit event for traceId: {}", event.getAuditLog().getTraceId());
            auditPort.save(event.getAuditLog());
        } catch (Exception e) {
            log.error("Failed to save audit log asynchronously", e);
        }
    }
}
