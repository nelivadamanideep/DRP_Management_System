package com.erpms.procurement.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_requests", indexes = {
        @Index(name = "idx_pr_status", columnList = "status"),
        @Index(name = "idx_pr_requester", columnList = "requested_by_user_id")
})
public class PurchaseRequestEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "request_number", nullable = false, unique = true, length = 60)
    private String requestNumber;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(columnDefinition = "text")
    private String justification;

    @Column(name = "project_id", length = 36)
    private String projectId;

    @Column(name = "requested_by_user_id", nullable = false, length = 36)
    private String requestedByUserId;

    @Column(name = "supplier_id", length = 36)
    private String supplierId;

    @Column(name = "estimated_cost", nullable = false, precision = 18, scale = 2)
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String status = "DRAFT"; // DRAFT, SUBMITTED, APPROVED, REJECTED, ORDERED, CLOSED

    @Column(name = "approver_user_id", length = 36)
    private String approverUserId;

    @Column(columnDefinition = "text")
    private String approverComments;

    public String getId() { return id; }
    public String getRequestNumber() { return requestNumber; }
    public void setRequestNumber(String v) { this.requestNumber = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getJustification() { return justification; }
    public void setJustification(String v) { this.justification = v; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String v) { this.projectId = v; }
    public String getRequestedByUserId() { return requestedByUserId; }
    public void setRequestedByUserId(String v) { this.requestedByUserId = v; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal v) { this.estimatedCost = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getApproverUserId() { return approverUserId; }
    public void setApproverUserId(String v) { this.approverUserId = v; }
    public String getApproverComments() { return approverComments; }
    public void setApproverComments(String v) { this.approverComments = v; }
}
