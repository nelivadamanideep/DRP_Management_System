package com.erpms.inventory.dto;

public record SupplierResponse(
        String id,
        String name,
        String contactEmail,
        String contactPhone,
        String address,
        String gstNumber,
        boolean active
) {}
