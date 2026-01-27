package com.lumos.core.port.out;

import java.util.List;

public interface VectorStorePort {
    /**
     * 保存向量到向量数据库
     * @param ideaId 关联的 Idea ID
     * @param vector 向量数据
     */
    void saveVector(Long ideaId, List<Double> vector);
}
