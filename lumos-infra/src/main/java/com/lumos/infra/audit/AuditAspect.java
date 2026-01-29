package com.lumos.infra.audit;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.lumos.core.annotation.Auditable;
import com.lumos.core.domain.AuditLog;
import com.lumos.core.event.AuditEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final TokenEstimator tokenEstimator;

    private static final String TRACE_ID_KEY = "traceId";

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 1. Trace ID 管理
        String traceId = MDC.get(TRACE_ID_KEY);
        boolean isRootTrace = false;
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID_KEY, traceId);
            isRootTrace = true;
        }

        Object result;
        try {
            // 2. 执行目标方法
            result = joinPoint.proceed();
        } catch (Throwable t) {
            // 同步异常直接记录
            recordAudit(traceId, auditable.operationType(), joinPoint.getArgs(), null, System.currentTimeMillis() - startTime, t);
            if (isRootTrace) MDC.remove(TRACE_ID_KEY);
            throw t;
        }

        // 3. 处理返回结果
        if (result instanceof Flux<?> fluxResult) {
            // --- 流式处理 ---
            // 必须捕获当前的 traceId，因为 Reactor 可能会切换线程，导致 MDC 丢失
            final String capturedTraceId = traceId;
            final boolean capturedIsRoot = isRootTrace;
            
            // 用于累加流式内容
            StringBuilder contentAccumulator = new StringBuilder();
            AtomicReference<Throwable> streamError = new AtomicReference<>();

            return fluxResult
                    .doOnNext(item -> {
                        if (item instanceof String str) {
                            contentAccumulator.append(str);
                        }
                    })
                    .doOnError(streamError::set)
                    .doFinally(signalType -> {
                        long duration = System.currentTimeMillis() - startTime;
                        // 流式结束时记录审计
                        // 注意：这里手动构建一个模拟结果对象传入 recordAudit
                        recordAudit(capturedTraceId, auditable.operationType(), joinPoint.getArgs(), contentAccumulator.toString(), duration, streamError.get());
                        if (capturedIsRoot) {
                            MDC.remove(TRACE_ID_KEY);
                        }
                    });
        } else {
            // --- 同步处理 ---
            long duration = System.currentTimeMillis() - startTime;
            recordAudit(traceId, auditable.operationType(), joinPoint.getArgs(), result, duration, null);
            if (isRootTrace) {
                MDC.remove(TRACE_ID_KEY);
            }
            return result;
        }
    }

    private void recordAudit(String traceId, String opType, Object[] args, Object result, long duration, Throwable error) {
        int promptTokens = 0;
        int completionTokens = 0;
        int totalTokens = 0;
        String model = "unknown";

        // 1. 尝试从结果中提取 Token 信息
        if (result instanceof ChatResponse chatResponse) {
            Usage usage = chatResponse.getMetadata().getUsage();
            if (usage != null) {
                promptTokens = usage.getPromptTokens().intValue();
                completionTokens = usage.getGenerationTokens().intValue();
                totalTokens = usage.getTotalTokens().intValue();
            } else {
                // ChatResponse 但无 Usage -> 兜底估算
                String content = chatResponse.getResult().getOutput().getContent();
                completionTokens = tokenEstimator.estimate(content);
            }
        } else if (result instanceof String content) {
            // 流式累加的纯文本 -> 兜底估算
            completionTokens = tokenEstimator.estimate(content);
        }
        
        // 2. 估算 Prompt Token (如果尚未获取)
        if (promptTokens == 0 && args.length > 0) {
            if (args[0] instanceof String prompt) {
                promptTokens = tokenEstimator.estimate(prompt);
            }
        }

        // 3. 计算 Total Token
        if (totalTokens == 0) {
            totalTokens = promptTokens + completionTokens;
        }

        AuditLog logEntry = AuditLog.builder()
                .traceId(traceId)
                .operationType(opType)
                .modelName(model) 
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .durationMs(duration)
                .createdAt(Instant.now())
                .build();

        eventPublisher.publishEvent(new AuditEvent(this, logEntry));
    }
}
