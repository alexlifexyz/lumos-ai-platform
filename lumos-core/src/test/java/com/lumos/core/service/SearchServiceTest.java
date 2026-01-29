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
import com.lumos.core.port.out.EmbeddingPort;
import com.lumos.core.port.out.IdeaRepositoryPort;
import com.lumos.core.port.out.VectorStorePort;
import com.lumos.core.port.out.RerankPort;

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

    @InjectMocks
    private SearchService searchService;

    @Test
    void search_ShouldFlowThroughAllComponents() {
        // Arrange
        String query = "AI and RAG";
        int limit = 5;
        List<Double> mockVector = List.of(0.1, 0.2);
        List<Long> mockIds = List.of(1L, 2L);
        List<Idea> mockIdeas = List.of(
            Idea.builder().id(1L).title("AI").build(),
            Idea.builder().id(2L).title("RAG").build()
        );

        when(embeddingPort.embed(query)).thenReturn(mockVector);
        when(vectorStorePort.searchHybrid(eq(mockVector), eq(query), any(Integer.class))).thenReturn(mockIds);
        when(ideaRepository.findAllByIds(mockIds)).thenReturn(mockIdeas);
        // Mock rerank as pass-through
        when(rerankPort.rerank(eq(query), any())).thenAnswer(invocation -> invocation.getArgument(1));

        // Act
        List<Idea> results = searchService.search(query, limit);

        // Assert
        assertEquals(2, results.size());
        
        verify(embeddingPort).embed(query);
        verify(vectorStorePort).searchHybrid(eq(mockVector), eq(query), any(Integer.class));
        verify(rerankPort).rerank(eq(query), eq(mockIdeas));
    }
}
