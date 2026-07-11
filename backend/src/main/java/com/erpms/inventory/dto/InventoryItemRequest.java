package com.erpms.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InventoryItemRequest(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        String category,
        String unit,
        String warehouseId,
        @NotNull BigDecimal stockQuantity,
        @NotNull BigDecimal reorderLevel,
        @NotNull BigDecimal unitCost,
        String supplierId
) {}
