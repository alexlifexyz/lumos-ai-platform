package com.lumos.core.domain;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 知识库文档：代表一个完整的源文件（如 PDF, Markdown）
 */
@Data
@Builder
public class Document {
    private Long id;
    private String uuid;
    private String filename;
    private String contentType; // 原始文件类型
    private String md5;         // 文件指纹，用于去重
    private Long size;          // 文件大小 (bytes)
    private Map<String, Object> metadata; // 提取的全局元数据
    private DocumentStatus status;        // 处理状态
    private String failureReason;         // 失败原因
    private Instant createdAt;
    private Instant updatedAt;

    public enum DocumentStatus {
        PENDING,    // 待处理
        PROCESSING, // 处理中
        COMPLETED,  // 处理完成
        FAILED      // 处理失败
    }
}
