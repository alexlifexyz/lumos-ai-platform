package com.lumos.infra.audit;

import org.springframework.stereotype.Component;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenEstimator {

    private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private final Encoding defaultEncoding = registry.getEncoding(EncodingType.CL100K_BASE); // OpenAI standard

    public int estimate(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            return defaultEncoding.countTokens(text);
        } catch (Exception e) {
            log.warn("Token estimation failed, returning length/4 approximate.", e);
            return text.length() / 4;
        }
    }
}
