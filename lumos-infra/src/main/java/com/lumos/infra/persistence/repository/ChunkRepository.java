package com.lumos.infra.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lumos.infra.persistence.entity.ChunkEntity;

public interface ChunkRepository extends JpaRepository<ChunkEntity, Long> {
    List<ChunkEntity> findByDocumentIdOrderByChunkIndexAsc(Long documentId);
}
