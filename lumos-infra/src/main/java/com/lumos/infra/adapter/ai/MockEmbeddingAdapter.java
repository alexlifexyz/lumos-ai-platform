package com.lumos.infra.adapter.ai;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.EmbeddingPort;

import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!openai & !ollama")
@Slf4j
public class MockEmbeddingAdapter implements EmbeddingPort {

    @Override
    public List<Double> embed(String text) {
        log.info("[MOCK] Generating dummy embedding for text: {}", text);
        // Return an empty list or random numbers, just to pass the flow
        return java.util.Collections.nCopies(1536, 0.0d);
    }
}
