package com.erpms.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PurchaseRequestRequest(
        @NotBlank String requestNumber,
        @NotBlank String title,
        String justification,
        String projectId,
        String supplierId,
        @NotNull BigDecimal estimatedCost
) {}
