package com.erpms.inventory.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String name,
        String contactEmail,
        String contactPhone,
        String address,
        String gstNumber,
        Boolean active
) {}
