package com.erpms.audit.aspect;

import com.erpms.audit.service.AuditLogService;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Captures a coarse audit trail for every REST controller invocation
 * (method + arguments + outcome) without polluting individual controllers.
 *
 * <p>Sensitive parameters (bodies containing "password" or "otp" fields)
 * are redacted before persistence.
 */
@Aspect
@Component
public class AuditLoggingAspect {

    private final AuditLogService auditLogService;

    public AuditLoggingAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Pointcut("within(com.erpms..controller..*)")
    public void anyController() {}

    @AfterReturning(pointcut = "anyController()", returning = "result")
    public void logSuccess(JoinPoint jp, Object result) {
        record(jp, 200, safeArgs(jp));
    }

    @AfterThrowing(pointcut = "anyController()", throwing = "ex")
    public void logFailure(JoinPoint jp, Throwable ex) {
        record(jp, 500, safeArgs(jp) + " | error=" + ex.getClass().getSimpleName() + ":" + ex.getMessage());
    }

    private void record(JoinPoint jp, Integer status, String metadata) {
        MethodSignature sig = (MethodSignature) jp.getSignature();
        String action = sig.getDeclaringType().getSimpleName() + "." + sig.getName();
        auditLogService.record(action, sig.getDeclaringType().getSimpleName(), null, status, metadata);
    }

    private String safeArgs(JoinPoint jp) {
        return "args=" + Arrays.stream(jp.getArgs())
                .map(a -> a == null ? "null" : redact(a.toString()))
                .collect(Collectors.joining(","));
    }

    private String redact(String v) {
        String lower = v.toLowerCase();
        if (lower.contains("password") || lower.contains("otp") || lower.contains("token")) {
            return "[REDACTED]";
        }
        return v.length() > 200 ? v.substring(0, 200) + "..." : v;
    }
}
