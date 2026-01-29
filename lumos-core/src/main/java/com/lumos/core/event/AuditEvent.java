package com.lumos.core.event;

import org.springframework.context.ApplicationEvent;

import com.lumos.core.domain.AuditLog;

import lombok.Getter;

/**
 * 审计事件：携带审计日志数据，用于异步持久化
 */
@Getter
public class AuditEvent extends ApplicationEvent {

    private final AuditLog auditLog;

    public AuditEvent(Object source, AuditLog auditLog) {
        super(source);
        this.auditLog = auditLog;
    }
}
