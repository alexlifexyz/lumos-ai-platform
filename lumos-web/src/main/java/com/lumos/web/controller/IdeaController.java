package com.lumos.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lumos.api.dto.CreateIdeaRequest;
import com.lumos.api.dto.IdeaResponse;
import com.lumos.core.domain.Idea;
import com.lumos.core.service.IdeaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ideas")
@RequiredArgsConstructor
@Validated
public class IdeaController {

    private final IdeaService ideaService;

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
