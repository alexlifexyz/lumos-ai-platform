package com.lumos.core.port.out;

import java.util.List;

public interface EmbeddingPort {
    /**
     * 将文本转换为向量
     * @param text 原始文本
     * @return 向量列表 (默认为 1536 维)
     */
    List<Double> embed(String text);
}
