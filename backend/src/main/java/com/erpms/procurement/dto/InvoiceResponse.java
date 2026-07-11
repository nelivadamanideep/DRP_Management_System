package com.erpms.procurement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(
        String id,
        String invoiceNumber,
        String purchaseOrderId,
        String supplierId,
        LocalDate invoiceDate,
        LocalDate dueDate,
        BigDecimal amount,
        String status
) {}
