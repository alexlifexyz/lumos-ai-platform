package com.lumos.core.port.out;

public interface GuardrailPort {
    /**
     * 检查并清洗文本
     * @param text 原始文本
     * @return 清洗后的文本（若包含敏感词可能被替换或拦截）
     */
    String sanitize(String text);

    /**
     * 是否包含严重违规内容（用于阻断）
     */
    boolean isToxic(String text);
}
