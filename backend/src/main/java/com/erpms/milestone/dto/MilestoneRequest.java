package com.erpms.milestone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MilestoneRequest(
        @NotBlank String projectId,
        @NotBlank @Size(max = 180) String name,
        String description,
        LocalDate dueDate,
        @NotNull @Min(0) @Max(100) Integer progressPercent,
        @NotBlank @Size(max = 40) String status
) {}