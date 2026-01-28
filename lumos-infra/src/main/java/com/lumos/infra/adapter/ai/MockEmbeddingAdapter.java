package com.lumos.infra.adapter.ai;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.EmbeddingPort;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnMissingBean(SpringAiEmbeddingAdapter.class)
@Slf4j
public class MockEmbeddingAdapter implements EmbeddingPort {

    @Override
    public List<Double> embed(String text) {
        log.info("[MOCK] Generating dummy embedding for text: {}", text);
        return Collections.nCopies(1536, 0.0d);
    }
}