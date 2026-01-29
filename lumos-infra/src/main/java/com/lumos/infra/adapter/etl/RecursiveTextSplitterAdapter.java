package com.lumos.infra.adapter.etl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.lumos.core.domain.Chunk;
import com.lumos.core.port.out.TextSplitterPort;

import lombok.extern.slf4j.Slf4j;

/**
 * 递归文本切分器适配器
 * 模仿 LangChain 的 RecursiveCharacterTextSplitter 逻辑
 */
@Component
@Slf4j
public class RecursiveTextSplitterAdapter implements TextSplitterPort {

    @Value("${lumos.etl.chunk-size:1000}")
    private int chunkSize;

    @Value("${lumos.etl.chunk-overlap:200}")
    private int chunkOverlap;

    private static final String[] SEPARATORS = {"\n\n", "\n", "。", ".", " ", ""};

    @Override
    public List<Chunk> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        List<String> rawChunks = splitRecursive(text, SEPARATORS, 0);
        List<Chunk> chunks = new ArrayList<>();
        
        for (int i = 0; i < rawChunks.size(); i++) {
            chunks.add(Chunk.builder()
                    .content(rawChunks.get(i))
                    .chunkIndex(i)
                    .build());
        }
        
        log.info("Split text into {} chunks (size={}, overlap={})", 
                chunks.size(), chunkSize, chunkOverlap);
        return chunks;
    }

    private List<String> splitRecursive(String text, String[] separators, int index) {
        if (text.length() <= chunkSize) {
            return List.of(text);
        }

        if (index >= separators.length) {
            // 兜底：如果所有分隔符都尝试完了还是太长，硬截断
            return hardSplit(text);
        }

        String separator = separators[index];
        String[] parts = text.split(java.util.regex.Pattern.quote(separator));
        
        List<String> finalChunks = new ArrayList<>();
        StringBuilder currentBuffer = new StringBuilder();

        for (String part : parts) {
            if (currentBuffer.length() + part.length() + separator.length() <= chunkSize) {
                if (!currentBuffer.isEmpty()) {
                    currentBuffer.append(separator);
                }
                currentBuffer.append(part);
            } else {
                if (!currentBuffer.isEmpty()) {
                    finalChunks.add(currentBuffer.toString());
                    // 处理 Overlap (简单实现：取当前 Buffer 的末尾作为下一块的开头)
                    int overlapStart = Math.max(0, currentBuffer.length() - chunkOverlap);
                    currentBuffer = new StringBuilder(currentBuffer.substring(overlapStart));
                    currentBuffer.append(separator);
                }
                currentBuffer.append(part);
                
                // 如果单部分本身就超过 chunkSize，递归下一级分隔符
                if (currentBuffer.length() > chunkSize) {
                    List<String> subChunks = splitRecursive(currentBuffer.toString(), separators, index + 1);
                    if (!subChunks.isEmpty()) {
                        // 移除 Buffer 中最后加入的部分，因为它已经被拆分了
                        finalChunks.addAll(subChunks.subList(0, subChunks.size() - 1));
                        currentBuffer = new StringBuilder(subChunks.get(subChunks.size() - 1));
                    }
                }
            }
        }
        
        if (!currentBuffer.isEmpty()) {
            finalChunks.add(currentBuffer.toString());
        }

        return finalChunks;
    }

    private List<String> hardSplit(String text) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += (chunkSize - chunkOverlap)) {
            chunks.add(text.substring(i, Math.min(i + chunkSize, text.length())));
        }
        return chunks;
    }
}
