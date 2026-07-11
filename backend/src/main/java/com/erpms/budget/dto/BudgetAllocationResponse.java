package com.erpms.budget.dto;

import java.math.BigDecimal;

public record BudgetAllocationResponse(
        String id,
        String projectId,
        int fiscalYear,
        String category,
        BigDecimal allocatedAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        String notes
) {}
