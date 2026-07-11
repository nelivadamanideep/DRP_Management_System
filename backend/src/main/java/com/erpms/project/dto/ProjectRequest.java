package com.erpms.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank @Size(max = 40) String projectCode,
        @NotBlank @Size(max = 220) String title,
        String summary,
        String departmentId,
        @NotBlank @Size(max = 30) String priority,
        @NotBlank @Size(max = 30) String riskLevel,
        @NotBlank @Size(max = 40) String status,
        LocalDate plannedStartDate,
        LocalDate plannedEndDate,
        @NotNull BigDecimal approvedBudget
) {}