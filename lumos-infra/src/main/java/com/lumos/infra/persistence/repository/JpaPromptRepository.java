package com.lumos.infra.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lumos.infra.persistence.entity.PromptEntity;

public interface JpaPromptRepository extends JpaRepository<PromptEntity, Long> {
    Optional<PromptEntity> findByCode(String code);
}
