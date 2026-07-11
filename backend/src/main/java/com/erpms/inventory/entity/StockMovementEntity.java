package com.erpms.inventory.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_movements", indexes = {
        @Index(name = "idx_movements_item", columnList = "item_id")
})
public class StockMovementEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "item_id", nullable = false, length = 36)
    private String itemId;

    @Column(nullable = false, length = 20)
    private String direction; // IN, OUT, ADJUST

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal quantity;

    @Column(length = 200)
    private String reason;

    @Column(name = "reference_id", length = 36)
    private String referenceId;

    @Column(name = "performed_by_user_id", length = 36)
    private String performedByUserId;

    public String getId() { return id; }
    public String getItemId() { return itemId; }
    public void setItemId(String v) { this.itemId = v; }
    public String getDirection() { return direction; }
    public void setDirection(String v) { this.direction = v; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal v) { this.quantity = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String v) { this.referenceId = v; }
    public String getPerformedByUserId() { return performedByUserId; }
    public void setPerformedByUserId(String v) { this.performedByUserId = v; }
}
