package com.erpms.team.dto;

public record ProjectTeamMemberResponse(
        String id,
        String projectId,
        String userId,
        String roleInProject,
        Integer allocationPercent,
        Boolean active
) {}