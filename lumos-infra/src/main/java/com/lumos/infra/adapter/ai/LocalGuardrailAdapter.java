package com.lumos.infra.adapter.ai;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.lumos.core.port.out.GuardrailPort;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LocalGuardrailAdapter implements GuardrailPort {

    // 简易敏感词列表 (演示用)
    private static final List<String> TOXIC_WORDS = List.of("毒品", "炸弹", "暴力", "自杀");

    // PII 正则 (手机号, 身份证, 邮箱)
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\b(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");

    @Override
    public String sanitize(String text) {
        if (text == null) return null;

        String sanitized = text;

        // 1. PII 脱敏
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll(" [PHONE_MASKED] ");
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll(" [EMAIL_MASKED] ");

        // 2. 敏感词简单替换 (非阻断模式)
        for (String word : TOXIC_WORDS) {
            sanitized = sanitized.replace(word, "***");
        }

        return sanitized;
    }

    @Override
    public boolean isToxic(String text) {
        if (text == null) return false;
        
        for (String word : TOXIC_WORDS) {
            if (text.contains(word)) {
                log.warn("Toxic content detected: contains word '{}'", word);
                return true;
            }
        }
        return false;
    }
}
