package com.lumos.infra.adapter.ai;

import java.util.List;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.lumos.core.port.out.EmbeddingPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("!local") // 仅在非 local 模式下激活
@RequiredArgsConstructor
@Slf4j
public class SpringAiEmbeddingAdapter implements EmbeddingPort {

    private final EmbeddingClient embeddingClient;

    @Override
    public List<Double> embed(String text) {
        log.debug("Calling AI Provider to embed text: {}", text.substring(0, Math.min(text.length(), 20)));
        return embeddingClient.embed(text);
    }
}
