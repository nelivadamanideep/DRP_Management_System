package com.erpms.procurement.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PurchaseRequestResponse(
        String id,
        String requestNumber,
        String title,
        String justification,
        String projectId,
        String requestedByUserId,
        String supplierId,
        BigDecimal estimatedCost,
        String status,
        String approverUserId,
        String approverComments,
        Instant createdAt,
        Instant updatedAt
) {}
