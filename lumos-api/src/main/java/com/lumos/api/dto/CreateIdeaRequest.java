package com.lumos.api.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public record CreateIdeaRequest(
    @NotBlank(message = "Title is required")
    String title,
    
    @NotBlank(message = "Content is required")
    String content,
    
    List<String> tags,
    
    Map<String, Object> metadata
) {}
