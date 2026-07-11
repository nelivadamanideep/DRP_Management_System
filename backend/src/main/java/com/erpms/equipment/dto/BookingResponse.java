package com.erpms.equipment.dto;

import java.time.Instant;

public record BookingResponse(
        String id,
        String equipmentId,
        String bookedByUserId,
        String projectId,
        Instant startTime,
        Instant endTime,
        String status,
        String purpose,
        Instant createdAt
) {}
