package com.erpms.audit.repository;

import com.erpms.audit.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, String> {
    Page<AuditLogEntity> findByUserIdOrderByOccurredAtDesc(String userId, Pageable pageable);
    Page<AuditLogEntity> findByActionOrderByOccurredAtDesc(String action, Pageable pageable);
    Page<AuditLogEntity> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
