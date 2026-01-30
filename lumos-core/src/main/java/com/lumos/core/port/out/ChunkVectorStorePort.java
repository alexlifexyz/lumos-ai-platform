package com.lumos.core.port.out;

import java.util.List;

public interface ChunkVectorStorePort {
    void saveChunkVector(Long chunkId, List<Double> vector);
    List<Long> searchChunkVectors(List<Double> queryVector, int limit);
    List<Long> searchChunksHybrid(List<Double> queryVector, String keyword, int limit);
    
    // Namespace aware search
    default List<Long> searchChunksHybrid(List<Double> queryVector, String keyword, int limit, String namespace) {
        return searchChunksHybrid(queryVector, keyword, limit);
    }
}
