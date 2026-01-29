package com.lumos.core.port.out;

import java.util.List;

public interface ChunkVectorStorePort {
    /**
     * 保存片段向量
     */
    void saveChunkVector(Long chunkId, List<Double> vector);

    /**
     * 搜索相似片段 ID
     */
    List<Long> searchChunkVectors(List<Double> queryVector, int limit);
}
