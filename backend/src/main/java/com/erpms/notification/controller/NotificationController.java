package com.erpms.notification.controller;

import com.erpms.common.web.PageResponse;
import com.erpms.notification.dto.NotificationResponse;
import com.erpms.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "In-app notification centre for the authenticated user")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List the current user's notifications (most recent first)")
    public PageResponse<NotificationResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return service.listForCurrentUser(page, size);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Return the number of unread notifications for the current user")
    public Map<String, Long> unreadCount() {
        return Map.of("count", service.unreadCountForCurrentUser());
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark a single notification as read")
    public void markRead(@PathVariable String id) {
        service.markRead(id);
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark every notification of the current user as read")
    public void markAllRead() {
        service.markAllReadForCurrentUser();
    }
}
