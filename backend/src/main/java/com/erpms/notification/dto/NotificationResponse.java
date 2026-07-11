package com.erpms.notification.dto;

import java.time.Instant;

public record NotificationResponse(
        String id,
        String recipientUserId,
        String category,
        String title,
        String body,
        String linkUrl,
        boolean read,
        Instant createdAt
) {}
