package com.lumos.core.domain;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 统一搜索结果：聚合自 Idea 和 Document Chunk
 */
@Data
@Builder
public class SearchResult {
    private String content;      // 命中内容
    private SourceType sourceType; // 来源类型: IDEA, DOCUMENT
    private String sourceName;   // 标题或文件名
    private String sourceId;     // 原始 UUID
    private Double score;        // 相关性评分
    private Map<String, Object> metadata; // 附加信息 (如页码)

    public enum SourceType {
        IDEA, DOCUMENT
    }
}
