package com.erpms.document.dto;

import java.time.Instant;

public record DocumentResponse(
        String id,
        String title,
        String description,
        String documentType,
        String status,
        String projectId,
        String folderId,
        String ownerUserId,
        String currentVersionId,
        boolean confidential,
        Instant createdAt,
        Instant updatedAt
) {}
