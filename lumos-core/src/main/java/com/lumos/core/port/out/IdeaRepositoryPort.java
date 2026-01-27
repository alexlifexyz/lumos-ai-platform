package com.lumos.core.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.lumos.core.domain.Idea;

public interface IdeaRepositoryPort {
    Idea save(Idea idea);
    Optional<Idea> findByUuid(UUID uuid);
    List<Idea> findAllByIds(List<Long> ids);
}
