package com.erpms.inventory.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "inventory_items", indexes = {
        @Index(name = "idx_items_sku", columnList = "sku", unique = true),
        @Index(name = "idx_items_warehouse", columnList = "warehouse_id")
})
public class InventoryItemEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 40)
    private String category;

    @Column(length = 30)
    private String unit; // pcs, kg, litre, etc.

    @Column(name = "warehouse_id", length = 36)
    private String warehouseId;

    @Column(name = "stock_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal stockQuantity = BigDecimal.ZERO;

    @Column(name = "reorder_level", nullable = false, precision = 18, scale = 4)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    @Column(name = "unit_cost", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "supplier_id", length = 36)
    private String supplierId;

    public String getId() { return id; }
    public String getSku() { return sku; }
    public void setSku(String v) { this.sku = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getUnit() { return unit; }
    public void setUnit(String v) { this.unit = v; }
    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String v) { this.warehouseId = v; }
    public BigDecimal getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(BigDecimal v) { this.stockQuantity = v; }
    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal v) { this.reorderLevel = v; }
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal v) { this.unitCost = v; }
    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String v) { this.supplierId = v; }
}
