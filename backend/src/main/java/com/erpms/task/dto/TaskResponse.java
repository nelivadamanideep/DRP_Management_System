package com.erpms.task.dto;

import java.time.LocalDate;

public record TaskResponse(
        String id,
        String projectId,
        String milestoneId,
        String title,
        String description,
        String assignedToUserId,
        String priority,
        String status,
        LocalDate dueDate,
        Integer progressPercent
) {}