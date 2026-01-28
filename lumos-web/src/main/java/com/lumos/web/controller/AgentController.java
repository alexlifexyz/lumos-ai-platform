package com.lumos.web.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumos.api.dto.AgentQueryRequest;
import com.lumos.api.dto.AgentQueryResponse;
import com.lumos.core.service.AgenticService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Agentic AI", description = "智能代理接口，支持 Text-to-SQL 自然语言查询")
@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AgentController {

    private final AgenticService agenticService;

    @Operation(summary = "智能数据查询", description = "使用自然语言查询数据库内容。Agent 会根据 Schema 自动生成并执行 SQL。")
    @PostMapping("/query")
    public AgentQueryResponse query(@RequestBody @Validated AgentQueryRequest request) {
        log.info("Received agent query: {}", request.query());
        return agenticService.executeQuery(request.query());
    }
}
