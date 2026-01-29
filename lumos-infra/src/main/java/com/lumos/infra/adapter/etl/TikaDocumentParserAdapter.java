package com.lumos.infra.adapter.etl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.DocumentParserPort;

import lombok.extern.slf4j.Slf4j;

/**
 * 基于 Apache Tika 的文档解析适配器
 */
@Component
@Slf4j
public class TikaDocumentParserAdapter implements DocumentParserPort {

    @Override
    public ParseResult parse(InputStream inputStream, String contentType) {
        log.info("Parsing document with contentType: {}", contentType);
        
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1); // -1 禁用内容大小限制
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();

        try {
            parser.parse(inputStream, handler, metadata, context);
            
            String rawText = handler.toString();
            String cleanedText = cleanContent(rawText);
            
            Map<String, Object> metadataMap = new HashMap<>();
            for (String name : metadata.names()) {
                metadataMap.put(name, metadata.get(name));
            }
            
            log.info("Successfully parsed document. Length: {}", cleanedText.length());
            return new ParseResult(cleanedText, metadataMap);
            
        } catch (Exception e) {
            log.error("Failed to parse document", e);
            throw new RuntimeException("Document parsing error: " + e.getMessage(), e);
        }
    }

    /**
     * 简单的文本清洗：去除多余空行和不可见字符
     */
    private String cleanContent(String text) {
        if (text == null) return "";
        // 1. 将多个连续换行替换为双换行
        // 2. 将多个空格替换为单空格
        // 3. 去除首尾空白
        return text.replaceAll("(?m)^\\s+$", "")
                   .replaceAll("\\n{3,}", "\n\n")
                   .replaceAll("[ ]{2,}", " ")
                   .trim();
    }
}
