package com.erpms.user.dto;

import jakarta.validation.constraints.Min;

public record UserProfileRequest(
        String designation,
        String departmentId,
        @Min(0) Integer experienceYears,
        String skills,
        String certifications,
        String phone
) {}