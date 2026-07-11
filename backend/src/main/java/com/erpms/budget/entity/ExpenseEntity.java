package com.erpms.budget.entity;

import com.erpms.common.audit.BaseAuditEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
        @Index(name = "idx_expenses_project", columnList = "project_id")
})
public class ExpenseEntity extends BaseAuditEntity {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "project_id", nullable = false, length = 36)
    private String projectId;

    @Column(name = "allocation_id", length = 36)
    private String allocationId;

    @Column(nullable = false, length = 80)
    private String category;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate = LocalDate.now();

    @Column(length = 400)
    private String description;

    @Column(name = "recorded_by_user_id", length = 36)
    private String recordedByUserId;

    public String getId() { return id; }
    public String getProjectId() { return projectId; }
    public void setProjectId(String v) { this.projectId = v; }
    public String getAllocationId() { return allocationId; }
    public void setAllocationId(String v) { this.allocationId = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal v) { this.amount = v; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate v) { this.expenseDate = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public String getRecordedByUserId() { return recordedByUserId; }
    public void setRecordedByUserId(String v) { this.recordedByUserId = v; }
}
