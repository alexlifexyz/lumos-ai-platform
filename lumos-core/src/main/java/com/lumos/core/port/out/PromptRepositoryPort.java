package com.lumos.core.port.out;

import java.util.Optional;
import com.lumos.core.domain.Prompt;

public interface PromptRepositoryPort {
    Optional<Prompt> findByCode(String code);
    Prompt save(Prompt prompt);
}
