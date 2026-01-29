package com.lumos.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumos.core.domain.AuditLog;
import com.lumos.core.domain.AuditStats;
import com.lumos.core.port.out.AuditPort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Governance", description = "系统治理与审计接口")
@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditPort auditPort;

    @Operation(summary = "查询审计流水", description = "获取最近的 AI 调用审计日志。")
    @GetMapping("/logs")
    public List<AuditLog> getAuditLogs(@RequestParam(defaultValue = "20") int limit) {
        return auditPort.findAll(limit);
    }

    @Operation(summary = "获取全局统计", description = "获取系统整体的 AI 调用统计数据（总次数、总 Token、平均耗时）。")
    @GetMapping("/stats")
    public AuditStats getGlobalStats() {
        return auditPort.getGlobalStats();
    }
}
