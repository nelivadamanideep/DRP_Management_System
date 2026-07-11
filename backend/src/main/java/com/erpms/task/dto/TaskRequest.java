package com.erpms.task.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record TaskRequest(
        @NotBlank String projectId,
        String milestoneId,
        @NotBlank @Size(max = 220) String title,
        String description,
        String assignedToUserId,
        @NotBlank @Size(max = 30) String priority,
        @NotBlank @Size(max = 40) String status,
        LocalDate dueDate,
        @NotNull @Min(0) @Max(100) Integer progressPercent
) {}