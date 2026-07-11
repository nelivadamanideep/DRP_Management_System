package com.erpms.budget.dto;

import java.math.BigDecimal;
import java.util.List;

public record BudgetSummaryResponse(
        String projectId,
        int fiscalYear,
        BigDecimal totalAllocated,
        BigDecimal totalSpent,
        BigDecimal remaining,
        List<BudgetAllocationResponse> allocations
) {}
