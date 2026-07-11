package com.erpms.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EquipmentRequest(
        @NotBlank @Size(max = 60) String assetTag,
        @NotBlank @Size(max = 200) String name,
        String description,
        String manufacturer,
        String modelNumber,
        String serialNumber,
        String departmentId,
        String laboratoryLocation,
        LocalDate purchaseDate,
        LocalDate nextCalibrationDate,
        String status
) {}
