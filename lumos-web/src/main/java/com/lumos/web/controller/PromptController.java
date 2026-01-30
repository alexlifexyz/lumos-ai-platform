package com.lumos.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumos.core.service.PromptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @GetMapping("/{code}")
    public String getPrompt(@PathVariable String code, @RequestParam(required = false) String defaultContent) {
        return promptService.getPromptContent(code, defaultContent);
    }

    @PutMapping("/{code}")
    public void updatePrompt(@PathVariable String code, @RequestBody String content) {
        promptService.updatePrompt(code, content);
    }
}
