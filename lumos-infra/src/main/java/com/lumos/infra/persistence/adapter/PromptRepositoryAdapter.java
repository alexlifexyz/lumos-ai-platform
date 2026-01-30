package com.lumos.infra.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.lumos.core.domain.Prompt;
import com.lumos.core.port.out.PromptRepositoryPort;
import com.lumos.infra.persistence.entity.PromptEntity;
import com.lumos.infra.persistence.repository.JpaPromptRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PromptRepositoryAdapter implements PromptRepositoryPort {

    private final JpaPromptRepository jpaPromptRepository;

    @Override
    public Optional<Prompt> findByCode(String code) {
        return jpaPromptRepository.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public Prompt save(Prompt prompt) {
        PromptEntity entity = toEntity(prompt);
        return toDomain(jpaPromptRepository.save(entity));
    }

    private Prompt toDomain(PromptEntity entity) {
        return Prompt.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .content(entity.getContent())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PromptEntity toEntity(Prompt prompt) {
        return PromptEntity.builder()
                .id(prompt.getId())
                .code(prompt.getCode())
                .name(prompt.getName())
                .content(prompt.getContent())
                .description(prompt.getDescription())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
}
