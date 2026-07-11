package com.erpms.equipment.dto;

import java.time.LocalDate;

public record EquipmentResponse(
        String id,
        String assetTag,
        String name,
        String description,
        String manufacturer,
        String modelNumber,
        String serialNumber,
        String departmentId,
        String laboratoryLocation,
        LocalDate purchaseDate,
        LocalDate nextCalibrationDate,
        String qrCodePayload,
        String status
) {}
