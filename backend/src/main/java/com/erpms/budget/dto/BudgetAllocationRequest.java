package com.erpms.budget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BudgetAllocationRequest(
        @NotBlank String projectId,
        int fiscalYear,
        @NotBlank String category,
        @NotNull BigDecimal allocatedAmount,
        String notes
) {}
