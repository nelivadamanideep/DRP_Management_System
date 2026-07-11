package com.erpms.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResponse(
        String id,
        String projectCode,
        String title,
        String summary,
        String departmentId,
        String priority,
        String riskLevel,
        String status,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        BigDecimal approvedBudget
) {}