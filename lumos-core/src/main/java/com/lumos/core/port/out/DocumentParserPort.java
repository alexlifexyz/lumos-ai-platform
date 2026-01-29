package com.lumos.core.port.out;

import java.io.InputStream;
import java.util.Map;

/**
 * 文档解析接口
 */
public interface DocumentParserPort {
    /**
     * 解析输入流并提取文本和元数据
     * @param inputStream 文件流
     * @param contentType MIME 类型
     * @return 解析结果，包含 "text" 正文和元数据 Map
     */
    ParseResult parse(InputStream inputStream, String contentType);

    record ParseResult(String text, Map<String, Object> metadata) {}
}
