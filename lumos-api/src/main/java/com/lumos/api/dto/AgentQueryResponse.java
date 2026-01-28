package com.lumos.api.dto;

import java.util.List;
import java.util.Map;

public record AgentQueryResponse(
    String answer,
    String sql,
    List<Map<String, Object>> data
) {}
