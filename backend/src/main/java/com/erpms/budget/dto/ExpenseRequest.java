package com.erpms.budget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String projectId,
        String allocationId,
        @NotBlank String category,
        @NotNull BigDecimal amount,
        LocalDate expenseDate,
        String description
) {}
