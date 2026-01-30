package com.lumos.core.service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lumos.core.domain.Chunk;
import com.lumos.core.domain.Document;
import com.lumos.core.domain.Document.DocumentStatus;
import com.lumos.core.port.out.ChunkVectorStorePort;
import com.lumos.core.port.out.DocumentParserPort;
import com.lumos.core.port.out.DocumentRepositoryPort;
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.TextSplitterPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeService {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentParserPort documentParser;
    private final TextSplitterPort textSplitter;
    private final EmbeddingPort embeddingPort;
    private final ChunkVectorStorePort chunkVectorStore;

    /**
     * 初始同步阶段：保存文档元数据
     */
    @Transactional
    public Document registerDocument(String filename, String contentType, long size, String namespace) {
        Document doc = Document.builder()
                .uuid(UUID.randomUUID().toString())
                .filename(filename)
                .contentType(contentType)
                .size(size)
                .namespace(namespace != null ? namespace : "default")
                .status(DocumentStatus.PENDING)
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
        
        return documentRepository.saveDocument(doc);
    }

    /**
     * 后台异步 Pipeline：解析 -> 切片 -> 向量化 -> 入库
     */
    @Async
    public void processDocument(Long documentId, InputStream inputStream) {
        log.info("Starting background processing for document ID: {}", documentId);
        
        Document doc = documentRepository.findDocumentById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        try {
            updateStatus(doc, DocumentStatus.PROCESSING, null);

            // 1. 解析 (ETL)
            DocumentParserPort.ParseResult parseResult = documentParser.parse(inputStream, doc.getContentType());
            doc.setMetadata(parseResult.metadata());
            
            // 2. 切片 (Chunking)
            List<Chunk> chunks = textSplitter.split(parseResult.text());
            chunks.forEach(c -> c.setDocumentId(documentId));
            
            // 3. 持久化片段
            documentRepository.saveChunks(chunks);
            
            // 4. 向量化并存储到向量库 (批量处理)
            // 获取数据库生成的 ID 后进行向量化
            List<Chunk> savedChunks = documentRepository.findChunksByDocumentId(documentId);
            for (Chunk chunk : savedChunks) {
                List<Double> vector = embeddingPort.embed(chunk.getContent());
                chunkVectorStore.saveChunkVector(chunk.getId(), vector);
            }

            updateStatus(doc, DocumentStatus.COMPLETED, null);
            log.info("Successfully processed document ID: {}. Total chunks: {}", documentId, chunks.size());

        } catch (Exception e) {
            log.error("Failed to process document ID: {}", documentId, e);
            updateStatus(doc, DocumentStatus.FAILED, e.getMessage());
        }
    }

    private void updateStatus(Document doc, DocumentStatus status, String failureReason) {
        doc.setStatus(status);
        doc.setFailureReason(failureReason);
        doc.setUpdatedAt(java.time.Instant.now());
        documentRepository.saveDocument(doc);
    }

    @Transactional(readOnly = true)
    public List<Document> getAllDocuments() {
        return documentRepository.findAllDocuments();
    }
}
