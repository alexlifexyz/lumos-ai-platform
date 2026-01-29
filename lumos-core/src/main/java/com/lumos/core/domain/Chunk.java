package com.lumos.core.domain;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 知识片段：文档切分后的最小检索单元
 */
@Data
@Builder
public class Chunk {
    private Long id;
    private Long documentId;      // 所属文档 ID
    private String content;       // 片段正文
    private Integer chunkIndex;   // 在文档中的顺序索引
    private Map<String, Object> metadata; // 片段级元数据 (如：页码、节标题)
    
    // 冗余字段或关联辅助，方便召回后快速定位文档
    private String documentUuid; 
    private String documentName;
}
