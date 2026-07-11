package com.erpms.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceRequest(
        @NotBlank String invoiceNumber,
        @NotBlank String purchaseOrderId,
        @NotBlank String supplierId,
        LocalDate invoiceDate,
        LocalDate dueDate,
        @NotNull BigDecimal amount
) {}
