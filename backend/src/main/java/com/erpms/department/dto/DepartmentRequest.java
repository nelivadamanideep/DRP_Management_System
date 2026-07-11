package com.erpms.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank @Size(max = 30) String code,
        @NotBlank @Size(max = 160) String name,
        String description,
        Boolean active
) {}