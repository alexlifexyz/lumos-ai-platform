package com.lumos.core.port.out;

import java.util.List;
import com.lumos.core.domain.Chunk;

/**
 * 文本切分接口
 */
public interface TextSplitterPort {
    /**
     * 将长文本切分为片段
     * @param text 待切分文本
     * @return 片段列表
     */
    List<Chunk> split(String text);
}
