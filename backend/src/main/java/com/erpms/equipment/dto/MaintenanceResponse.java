package com.erpms.equipment.dto;

import java.time.LocalDate;

public record MaintenanceResponse(
        String id,
        String equipmentId,
        String performedByUserId,
        LocalDate performedOn,
        String activity,
        String notes,
        LocalDate nextDueOn
) {}
