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

    /**
     * 针对片段的混合检索 (向量 + 全文)
     * @return 匹配的片段 ID 列表
     */
    List<Long> searchChunksHybrid(List<Double> queryVector, String keyword, int limit);
}
