package com.erpms.audit.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_user", columnList = "user_id"),
        @Index(name = "idx_audit_ts", columnList = "occurred_at"),
        @Index(name = "idx_audit_target", columnList = "target_type,target_id")
})
public class AuditLogEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt = Instant.now();

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "user_email", length = 200)
    private String userEmail;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(name = "target_type", length = 80)
    private String targetType;

    @Column(name = "target_id", length = 120)
    private String targetId;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "request_uri", length = 400)
    private String requestUri;

    @Column(name = "ip_address", length = 60)
    private String ipAddress;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(columnDefinition = "text")
    private String metadata;

    public String getId() { return id; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant v) { this.occurredAt = v; }
    public String getUserId() { return userId; }
    public void setUserId(String v) { this.userId = v; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String v) { this.userEmail = v; }
    public String getAction() { return action; }
    public void setAction(String v) { this.action = v; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String v) { this.targetType = v; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String v) { this.targetId = v; }
    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String v) { this.httpMethod = v; }
    public String getRequestUri() { return requestUri; }
    public void setRequestUri(String v) { this.requestUri = v; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String v) { this.ipAddress = v; }
    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer v) { this.statusCode = v; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String v) { this.metadata = v; }
}
