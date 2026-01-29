package com.lumos.infra.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.lumos.core.domain.Idea;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.infra.persistence.entity.IdeaEntity;
import com.lumos.infra.persistence.repository.IdeaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdeaRepositoryAdapter implements IdeaRepositoryPort {

    private final IdeaRepository jpaRepository;

    @Override
    public Idea save(Idea idea) {
        IdeaEntity entity = toEntity(idea);
        IdeaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Idea> findByUuid(UUID uuid) {
        return jpaRepository.findByUuid(uuid).map(this::toDomain);
    }

    @Override
    public List<Idea> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<IdeaEntity> entities = jpaRepository.findAllById(ids);
        // 按输入的 ID 顺序排序，保持检索结果的相关性顺序
        return ids.stream()
                .flatMap(id -> entities.stream().filter(e -> e.getId().equals(id)))
                .map(this::toDomain)
                .toList();
    }

    // Simple manual mapper to avoid MapStruct dependency overhead for now
    private IdeaEntity toEntity(Idea domain) {
        return IdeaEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .title(domain.getTitle())
                .content(domain.getContent())
                .tags(domain.getTags())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Idea toDomain(IdeaEntity entity) {
        return Idea.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .title(entity.getTitle())
                .content(entity.getContent())
                .tags(entity.getTags())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
