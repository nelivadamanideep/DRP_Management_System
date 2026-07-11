package com.erpms.document.dto;

import java.time.Instant;

public record FolderResponse(
        String id,
        String name,
        String parentId,
        String projectId,
        String path,
        Instant createdAt
) {}
