package com.lumos.infra.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Document;
import com.lumos.core.domain.Chunk;
import com.lumos.core.port.out.DocumentRepositoryPort;
import com.lumos.infra.persistence.entity.DocumentEntity;
import com.lumos.infra.persistence.entity.ChunkEntity;
import com.lumos.infra.persistence.repository.DocumentRepository;
import com.lumos.infra.persistence.repository.ChunkRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;

    @Override
    @Transactional
    public Document saveDocument(Document doc) {
        DocumentEntity entity = toEntity(doc);
        DocumentEntity saved = documentRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Document> findDocumentById(Long id) {
        return documentRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Document> findDocumentByUuid(String uuid) {
        return documentRepository.findByUuid(UUID.fromString(uuid)).map(this::toDomain);
    }

    @Override
    @Transactional
    public void saveChunks(List<Chunk> chunks) {
        List<ChunkEntity> entities = chunks.stream()
                .map(this::toChunkEntity)
                .toList();
        chunkRepository.saveAll(entities);
    }

    @Override
    public List<Chunk> findChunksByDocumentId(Long documentId) {
        return chunkRepository.findByDocumentIdOrderByChunkIndexAsc(documentId).stream()
                .map(this::toChunkDomain)
                .toList();
    }

    @Override
    public List<Chunk> findAllChunksByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        
        List<ChunkEntity> entities = chunkRepository.findAllById(ids);
        
        // 提取去重后的文档 ID，批量获取文档元数据
        List<Long> docIds = entities.stream().map(ChunkEntity::getDocumentId).distinct().toList();
        List<DocumentEntity> docs = documentRepository.findAllById(docIds);
        java.util.Map<Long, DocumentEntity> docMap = docs.stream()
                .collect(java.util.stream.Collectors.toMap(DocumentEntity::getId, d -> d));

        // 维持输入的 ID 顺序
        return ids.stream()
                .flatMap(id -> entities.stream().filter(e -> e.getId().equals(id)))
                .map(e -> {
                    Chunk chunk = toChunkDomain(e);
                    DocumentEntity doc = docMap.get(e.getDocumentId());
                    if (doc != null) {
                        chunk.setDocumentUuid(doc.getUuid().toString());
                        chunk.setDocumentName(doc.getFilename());
                    }
                    return chunk;
                })
                .toList();
    }

    private DocumentEntity toEntity(Document domain) {
        return DocumentEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid() != null ? UUID.fromString(domain.getUuid()) : null)
                .filename(domain.getFilename())
                .contentType(domain.getContentType())
                .md5(domain.getMd5())
                .size(domain.getSize())
                .metadata(domain.getMetadata())
                .status(domain.getStatus())
                .failureReason(domain.getFailureReason())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private Document toDomain(DocumentEntity entity) {
        return Document.builder()
                .id(entity.getId())
                .uuid(entity.getUuid().toString())
                .filename(entity.getFilename())
                .contentType(entity.getContentType())
                .md5(entity.getMd5())
                .size(entity.getSize())
                .metadata(entity.getMetadata())
                .status(entity.getStatus())
                .failureReason(entity.getFailureReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ChunkEntity toChunkEntity(Chunk domain) {
        return ChunkEntity.builder()
                .documentId(domain.getDocumentId())
                .content(domain.getContent())
                .chunkIndex(domain.getChunkIndex())
                .metadata(domain.getMetadata())
                .createdAt(java.time.Instant.now())
                .build();
    }

    private Chunk toChunkDomain(ChunkEntity entity) {
        return Chunk.builder()
                .id(entity.getId())
                .documentId(entity.getDocumentId())
                .content(entity.getContent())
                .chunkIndex(entity.getChunkIndex())
                .metadata(entity.getMetadata())
                .build();
    }
}
