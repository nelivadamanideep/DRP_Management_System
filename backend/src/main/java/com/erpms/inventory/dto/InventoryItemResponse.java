package com.erpms.inventory.dto;

import java.math.BigDecimal;

public record InventoryItemResponse(
        String id,
        String sku,
        String name,
        String description,
        String category,
        String unit,
        String warehouseId,
        BigDecimal stockQuantity,
        BigDecimal reorderLevel,
        BigDecimal unitCost,
        String supplierId,
        boolean lowStock
) {}
