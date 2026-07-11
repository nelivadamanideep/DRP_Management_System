package com.erpms.department.dto;

public record DepartmentResponse(
        String id,
        String code,
        String name,
        String description,
        Boolean active
) {}