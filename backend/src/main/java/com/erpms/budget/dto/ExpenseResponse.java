package com.erpms.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        String id,
        String projectId,
        String allocationId,
        String category,
        BigDecimal amount,
        LocalDate expenseDate,
        String description,
        String recordedByUserId
) {}
