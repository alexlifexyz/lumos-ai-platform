package com.lumos.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lumos.api.dto.CreateIdeaRequest;
import com.lumos.api.dto.IdeaResponse;
import com.lumos.core.domain.Idea;
import com.lumos.core.domain.SearchResult;
import com.lumos.core.service.IdeaService;
import com.lumos.core.service.SearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Ideas", description = "知识点管理接口")
@RestController
@RequestMapping("/api/v1/ideas")
@RequiredArgsConstructor
@Validated
@Slf4j
public class IdeaController {

    private final IdeaService ideaService;
    private final SearchService searchService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdeaResponse createIdea(@RequestBody @Validated CreateIdeaRequest request) {
        Idea idea = Idea.builder()
                .title(request.title())
                .content(request.content())
                .tags(request.tags())
                .metadata(request.metadata())
                .build();
        
        Idea savedIdea = ideaService.createIdea(idea);
        return toResponse(savedIdea);
    }

    @GetMapping("/{uuid}")
    public IdeaResponse getIdea(@PathVariable UUID uuid) {
        Idea idea = ideaService.getIdea(uuid);
        return toResponse(idea);
    }

    @Operation(summary = "全域集成搜索", description = "在 Idea 和知识库文档片段中进行混合检索。")
    @GetMapping("/search")
    public List<SearchResult> search(
            @RequestParam(value = "query", required = false, defaultValue = "") String query, 
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        log.info("Received search request with query: [{}], limit: [{}]", query, limit);
        if (query.isBlank()) {
            return List.of();
        }
        return searchService.search(query, limit);
    }

    private IdeaResponse toResponse(Idea idea) {
        return new IdeaResponse(
                idea.getUuid(),
                idea.getTitle(),
                idea.getContent(),
                idea.getTags(),
                idea.getMetadata(),
                idea.getCreatedAt(),
                idea.getUpdatedAt()
        );
    }
}
