package com.lumos.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

@ExtendWith(MockitoExtension.class)
class IdeaServiceTest {

    @Mock
    private IdeaRepositoryPort ideaRepository;
    @Mock
    private EmbeddingPort embeddingPort;
    @Mock
    private VectorStorePort vectorStorePort;

    @InjectMocks
    private IdeaService ideaService;

    @Test
    void createIdea_ShouldPersistIdeaAndVector() {
        // Arrange
        Idea inputIdea = Idea.builder().title("Test Idea").content("Test Content").build();
        Idea savedIdea = Idea.builder().id(1L).title("Test Idea").content("Test Content").build();
        List<Double> vector = List.of(0.1, 0.2, 0.3);

        when(ideaRepository.save(any(Idea.class))).thenReturn(savedIdea);
        when(embeddingPort.embed("Test Content")).thenReturn(vector);

        // Act
        Idea result = ideaService.createIdea(inputIdea);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        verify(ideaRepository).save(any(Idea.class));
        verify(embeddingPort).embed("Test Content");
        verify(vectorStorePort).saveVector(1L, vector);
    }
}
