package com.erpms.inventory.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record StockMovementResponse(
        String id,
        String itemId,
        String direction,
        BigDecimal quantity,
        String reason,
        String referenceId,
        String performedByUserId,
        Instant createdAt
) {}
