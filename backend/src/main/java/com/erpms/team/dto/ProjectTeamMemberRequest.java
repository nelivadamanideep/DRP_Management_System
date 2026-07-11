package com.erpms.team.dto;

import jakarta.validation.constraints.*;

public record ProjectTeamMemberRequest(
        @NotBlank String projectId,
        @NotBlank String userId,
        @NotBlank @Size(max = 80) String roleInProject,
        @NotNull @Min(0) @Max(100) Integer allocationPercent,
        Boolean active
) {}