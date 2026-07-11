package com.erpms.user.dto;

public record UserProfileResponse(
        String id,
        String userId,
        String designation,
        String departmentId,
        Integer experienceYears,
        String skills,
        String certifications,
        String phone
) {}