package com.erpms.inventory.dto;

public record WarehouseResponse(
        String id,
        String code,
        String name,
        String location,
        String managerUserId,
        boolean active
) {}
