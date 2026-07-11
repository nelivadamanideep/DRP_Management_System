package com.erpms.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FolderCreateRequest(
        @NotBlank @Size(max = 180) String name,
        String parentId,
        String projectId
) {}
