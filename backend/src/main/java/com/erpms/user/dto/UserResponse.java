package com.erpms.user.dto;

public record UserResponse(
        String id,
        String email,
        String fullName,
        String role,
        String status
) {}