package com.erpms.procurement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PurchaseOrderResponse(
        String id,
        String poNumber,
        String requestId,
        String supplierId,
        String issuedByUserId,
        LocalDate issuedOn,
        LocalDate expectedDelivery,
        BigDecimal totalAmount,
        String status,
        String notes
) {}
