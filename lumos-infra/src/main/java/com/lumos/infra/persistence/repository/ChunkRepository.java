package com.lumos.infra.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lumos.infra.persistence.entity.ChunkEntity;

public interface ChunkRepository extends JpaRepository<ChunkEntity, Long> {
    List<ChunkEntity> findByDocumentIdOrderByChunkIndexAsc(Long documentId);
    
    // 查询指定范围内的 chunks (用于上下文窗口扩展)
    List<ChunkEntity> findByDocumentIdAndChunkIndexBetweenOrderByChunkIndexAsc(Long documentId, int startIndex, int endIndex);
}
