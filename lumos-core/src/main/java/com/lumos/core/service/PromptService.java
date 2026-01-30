package com.lumos.core.service;

import org.springframework.stereotype.Service;

import com.lumos.core.domain.Prompt;
import com.lumos.core.port.out.PromptRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptService {

    private final PromptRepositoryPort promptRepository;

    public String getPromptContent(String code, String defaultContent) {
        return promptRepository.findByCode(code)
                .map(Prompt::getContent)
                .orElseGet(() -> {
                    log.warn("Prompt with code {} not found, using default content", code);
                    return defaultContent;
                });
    }

    public void updatePrompt(String code, String content) {
        Prompt prompt = promptRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Prompt not found: " + code));
        
        prompt.setContent(content);
        prompt.setUpdatedAt(java.time.Instant.now());
        promptRepository.save(prompt);
        log.info("Prompt {} updated successfully", code);
    }
}
