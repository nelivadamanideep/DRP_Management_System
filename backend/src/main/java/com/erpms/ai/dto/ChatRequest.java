package com.erpms.ai.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ChatRequest(
        @NotEmpty List<ChatTurn> messages
) {
    public record ChatTurn(String role, String content) {}
}
