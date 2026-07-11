package com.erpms.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StockMovementRequest(
        @NotBlank String itemId,
        @NotBlank String direction,
        @NotNull BigDecimal quantity,
        String reason,
        String referenceId
) {}
