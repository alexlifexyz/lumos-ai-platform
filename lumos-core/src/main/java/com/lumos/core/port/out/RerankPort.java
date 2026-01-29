package com.lumos.core.port.out;

import java.util.List;
import com.lumos.core.domain.SearchResult;

/**
 * 重排序接口：用于对初步检索结果进行更精细的相关性评分
 */
public interface RerankPort {
    /**
     * 对检索出的结果列表进行重排序
     * @param query 用户查询
     * @param candidates 初步检索出的候选列表 (包含不同来源)
     * @return 重新排序后的列表（按相关性降序）
     */
    List<SearchResult> rerank(String query, List<SearchResult> candidates);
}
