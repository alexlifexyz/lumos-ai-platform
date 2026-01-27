package com.lumos.infra.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lumos.infra.persistence.entity.IdeaEntity;

@Repository
public interface IdeaRepository extends JpaRepository<IdeaEntity, Long> {
    Optional<IdeaEntity> findByUuid(UUID uuid);
}
