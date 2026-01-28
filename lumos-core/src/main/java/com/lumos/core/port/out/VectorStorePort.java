package com.lumos.core.port.out;

import java.util.List;

public interface VectorStorePort {
    /**
     * 保存向量到向量数据库
     * @param ideaId 关联的 Idea ID
     * @param vector 向量数据
     */
    void saveVector(Long ideaId, List<Double> vector);

    /**
     * 搜索相似向量
     * @param queryVector 查询向量
     * @param limit 返回数量
     * @return 匹配的 Idea ID 列表 (按相似度排序)
     */
    List<Long> searchVectors(List<Double> queryVector, int limit);

    /**
     * 混合检索 (向量相似度 + 关键词匹配)
     * @param queryVector 查询向量
     * @param keyword 原始关键词
     * @param limit 返回数量
     * @return 匹配的 ID 列表
     */
    List<Long> searchHybrid(List<Double> queryVector, String keyword, int limit);
}
