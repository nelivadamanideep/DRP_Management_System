package com.erpms.ai.dto;

public record AiInsightResponse(
        String kind,
        String targetId,
        String content
) {}
