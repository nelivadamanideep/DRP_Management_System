package com.erpms.document.dto;

import java.time.Instant;

public record DocumentVersionResponse(
        String id,
        String documentId,
        int versionNumber,
        String fileName,
        String contentType,
        long sizeBytes,
        String contentSha256,
        String uploadedByUserId,
        String changelog,
        Instant createdAt
) {}
