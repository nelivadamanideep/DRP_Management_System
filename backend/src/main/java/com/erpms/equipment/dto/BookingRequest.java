package com.erpms.equipment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record BookingRequest(
        @NotNull String equipmentId,
        String projectId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        String purpose
) {}
