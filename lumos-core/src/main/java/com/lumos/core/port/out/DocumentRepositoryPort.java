package com.lumos.core.port.out;

import java.util.Optional;
import com.lumos.core.domain.Document;
import com.lumos.core.domain.Chunk;
import java.util.List;

public interface DocumentRepositoryPort {
    Document saveDocument(Document document);
    Optional<Document> findDocumentById(Long id);
    Optional<Document> findDocumentByUuid(String uuid);
    
    void saveChunks(List<Chunk> chunks);
    List<Chunk> findChunksByDocumentId(Long documentId);
    List<Chunk> findAllChunksByIds(List<Long> ids);
}
