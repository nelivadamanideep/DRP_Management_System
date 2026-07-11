package com.erpms.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseOrderRequest(
        @NotBlank String poNumber,
        String requestId,
        @NotBlank String supplierId,
        LocalDate expectedDelivery,
        @NotNull BigDecimal totalAmount,
        String notes
) {}
