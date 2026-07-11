package com.erpms.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 160) String name,
        String location,
        String managerUserId,
        Boolean active
) {}
