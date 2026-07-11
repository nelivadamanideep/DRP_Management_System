package com.erpms.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentCreateRequest(
        @NotBlank @Size(max = 240) String title,
        String description,
        @NotBlank @Size(max = 40) String documentType,
        String projectId,
        String folderId,
        Boolean confidential
) {}
