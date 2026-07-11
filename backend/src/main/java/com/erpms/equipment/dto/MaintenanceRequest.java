package com.erpms.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MaintenanceRequest(
        @NotNull String equipmentId,
        String performedByUserId,
        @NotNull LocalDate performedOn,
        @NotBlank String activity,
        String notes,
        LocalDate nextDueOn
) {}
