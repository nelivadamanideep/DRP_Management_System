package com.erpms.audit.service;

import com.erpms.audit.entity.AuditLogEntity;
import com.erpms.audit.repository.AuditLogRepository;
import com.erpms.common.security.SecurityUtils;
import com.erpms.common.web.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Persists {@link AuditLogEntity} rows for security-relevant activity.
 *
 * <p>Writes happen asynchronously so audit persistence never blocks the
 * request/response cycle. Query APIs remain synchronous and paginated.
 */
@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository repository;

    public AuditLogService(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Async("erpmsTaskExecutor")
    @Transactional
    public void record(String action, String targetType, String targetId, Integer statusCode, String metadata) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setAction(action);
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setStatusCode(statusCode);
        entity.setMetadata(metadata);
        entity.setUserId(SecurityUtils.currentUserIdOrNull());
        entity.setUserEmail(SecurityUtils.currentEmailOrNull());

        HttpServletRequest req = currentRequest();
        if (req != null) {
            entity.setHttpMethod(req.getMethod());
            entity.setRequestUri(req.getRequestURI());
            entity.setIpAddress(clientIp(req));
        }
        try {
            repository.save(entity);
        } catch (Exception ex) {
            log.warn("[audit] persistence failed: {}", ex.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogEntity> list(String userId, String action, int page, int size) {
        Pageable p = PageRequest.of(page, size);
        if (userId != null && !userId.isBlank()) {
            return PageResponse.from(repository.findByUserIdOrderByOccurredAtDesc(userId, p));
        }
        if (action != null && !action.isBlank()) {
            return PageResponse.from(repository.findByActionOrderByOccurredAtDesc(action, p));
        }
        return PageResponse.from(repository.findAllByOrderByOccurredAtDesc(p));
    }

    private HttpServletRequest currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) return sra.getRequest();
        return null;
    }

    private String clientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        return forwarded != null && !forwarded.isBlank()
                ? forwarded.split(",")[0].trim()
                : req.getRemoteAddr();
    }
}
