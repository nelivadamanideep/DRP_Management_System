package com.erpms.project.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "project_code", nullable = false, unique = true, length = 40)
    private String projectCode;

    @Column(nullable = false, length = 220)
    private String title;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(name = "department_id", length = 36)
    private String departmentId;

    @Column(nullable = false, length = 30)
    private String priority;

    @Column(name = "risk_level", nullable = false, length = 30)
    private String riskLevel;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "planned_start_date")
    private LocalDate plannedStartDate;

    @Column(name = "planned_end_date")
    private LocalDate plannedEndDate;

    @Column(name = "approved_budget", nullable = false, precision = 18, scale = 2)
    private BigDecimal approvedBudget = BigDecimal.ZERO;

    public String getId() { return id; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPlannedStartDate() { return plannedStartDate; }
    public void setPlannedStartDate(LocalDate plannedStartDate) { this.plannedStartDate = plannedStartDate; }

    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public void setPlannedEndDate(LocalDate plannedEndDate) { this.plannedEndDate = plannedEndDate; }

    public BigDecimal getApprovedBudget() { return approvedBudget; }
    public void setApprovedBudget(BigDecimal approvedBudget) { this.approvedBudget = approvedBudget; }
}