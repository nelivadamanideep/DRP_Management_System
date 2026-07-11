package com.erpms.procurement.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders", indexes = {
        @Index(name = "idx_po_status", columnList = "status"),
        @Index(name = "idx_po_request", columnList = "request_id")
})
public class PurchaseOrderEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "po_number", nullable = false, unique = true, length = 60)
    private String poNumber;

    @Column(name = "request_id", length = 36)
    private String requestId;

    @Column(name = "supplier_id", nullable = false, length = 36)
    private String supplierId;

    @Column(name = "issued_by_user_id", nullable = false, length = 36)
    private String issuedByUserId;

    @Column(name = "issued_on", nullable = false)
    private LocalDate issuedOn = LocalDate.now();

    @Column(name = "expected_delivery")
    private LocalDate expectedDelivery;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    private String status = "ISSUED"; // ISSUED, DELIVERED, INVOICED, CLOSED, CANCELLED

    @Column(columnDefinition = "text")
    private String notes;

    public String getId() { return id; }
    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String v) { this.poNumber = v; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String v) { this.requestId = v; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
    public String getIssuedByUserId() { return issuedByUserId; }
    public void setIssuedByUserId(String v) { this.issuedByUserId = v; }
    public LocalDate getIssuedOn() { return issuedOn; }
    public void setIssuedOn(LocalDate v) { this.issuedOn = v; }
    public LocalDate getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(LocalDate v) { this.expectedDelivery = v; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal v) { this.totalAmount = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
