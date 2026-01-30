package com.lumos.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lumos.core.domain.Idea;
import com.lumos.core.domain.SearchResult;
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.core.port.out.VectorStorePort;
import com.lumos.core.port.out.RerankPort;
import com.lumos.core.port.out.ChunkVectorStorePort;
import com.lumos.core.port.out.DocumentRepositoryPort;

import lombok.RequiredArgsConstructor;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private EmbeddingPort embeddingPort;
    @Mock
    private VectorStorePort vectorStorePort;
    @Mock
    private IdeaRepositoryPort ideaRepository;
    @Mock
    private RerankPort rerankPort;
    @Mock
    private ChunkVectorStorePort chunkVectorStore;
    @Mock
    private DocumentRepositoryPort documentRepository;

    @InjectMocks
    private SearchService searchService;

    @Test
    void search_ShouldExpandContext_WhenMetadataIsPresent() {
        // Arrange
        String query = "Context";
        int limit = 5;
        List<Double> mockVector = List.of(0.1);
        List<Long> mockChunkIds = List.of(10L);

        // 模拟返回的 Chunk，包含 metadata
        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("internal_doc_id", 100L);
        metadata.put("chunk_index", 5);

        com.lumos.core.domain.Chunk originalChunk = com.lumos.core.domain.Chunk.builder()
                .id(10L)
                .documentId(100L)
                .documentName("Doc")
                .content("Target Chunk")
                .chunkIndex(5)
                .metadata(metadata)
                .build();

        // 模拟上下文窗口 (前、中、后)
        List<com.lumos.core.domain.Chunk> expandedChunks = List.of(
                com.lumos.core.domain.Chunk.builder().content("Prev Chunk").build(),
                originalChunk,
                com.lumos.core.domain.Chunk.builder().content("Next Chunk").build()
        );

        when(embeddingPort.embed(anyString())).thenReturn(mockVector);
        // 为了简化，不返回 Idea
        when(vectorStorePort.searchHybrid(any(), anyString(), any(Integer.class))).thenReturn(List.of());
        when(chunkVectorStore.searchChunksHybrid(any(), anyString(), any(Integer.class))).thenReturn(mockChunkIds);
        when(ideaRepository.findAllByIds(any())).thenReturn(List.of());
        when(documentRepository.findAllChunksByIds(mockChunkIds)).thenReturn(List.of(originalChunk));
        
        // Mock rerank as pass-through
        when(rerankPort.rerank(eq(query), any())).thenAnswer(invocation -> invocation.getArgument(1));

        // Mock 上下文查询
        when(documentRepository.findChunksByDocumentIdAndIndexRange(100L, 4, 6)).thenReturn(expandedChunks);

        // Act
        List<SearchResult> results = searchService.search(query, limit);

        // Assert
        assertEquals(1, results.size());
        SearchResult result = results.get(0);
        
        // 验证内容是否被拼接 (Prev ... Target ... Next)
        assert(result.getContent().contains("Prev Chunk"));
        assert(result.getContent().contains("Target Chunk"));
        assert(result.getContent().contains("Next Chunk"));
        assertEquals(true, result.getMetadata().get("is_expanded"));
    }
}
