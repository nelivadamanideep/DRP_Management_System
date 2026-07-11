package com.erpms.milestone.dto;

import java.time.LocalDate;

public record MilestoneResponse(
        String id,
        String projectId,
        String name,
        String description,
        LocalDate dueDate,
        Integer progressPercent,
        String status
) {}