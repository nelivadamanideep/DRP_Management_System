package com.erpms.budget.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "budget_allocations", indexes = {
        @Index(name = "idx_alloc_project", columnList = "project_id"),
        @Index(name = "idx_alloc_year", columnList = "fiscal_year")
})
public class BudgetAllocationEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    @Column(nullable = false, length = 80)
    private String category; // MANPOWER, EQUIPMENT, CONSUMABLES, TRAVEL, MISC

    @Column(name = "allocated_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    @Column(name = "spent_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(columnDefinition = "text")
    private String notes;

    public String getId() { return id; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String v) { this.projectId = v; }
    public int getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(int v) { this.fiscalYear = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal v) { this.allocatedAmount = v; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal v) { this.spentAmount = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
