package com.lumos.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentQueryRequest(
    @NotBlank(message = "查询内容不能为空")
    String query
) {}
