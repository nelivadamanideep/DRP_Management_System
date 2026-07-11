package com.erpms.notification.service;

import com.erpms.common.exception.ResourceNotFoundException;
import com.erpms.common.security.SecurityUtils;
import com.erpms.common.web.PageResponse;
import com.erpms.notification.dto.NotificationResponse;
import com.erpms.notification.entity.NotificationEntity;
import com.erpms.notification.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Central façade for delivering notifications.
 *
 * <p>Domain code should never call {@link NotificationRepository} or
 * {@link EmailSender} directly — always go through this service so the
 * in-app row and the email are consistent.
 */
@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailSender emailSender;

    public NotificationService(NotificationRepository repository, EmailSender emailSender) {
        this.repository = repository;
        this.emailSender = emailSender;
    }

    /**
     * Persist an in-app notification and (optionally) fire an email copy.
     *
     * @param recipientEmail can be null for pure in-app entries
     */
    @Transactional
    public NotificationResponse deliver(String recipientUserId, String recipientEmail,
                                        String category, String title, String body, String linkUrl) {
        NotificationEntity entity = new NotificationEntity();
        entity.setRecipientUserId(recipientUserId);
        entity.setCategory(category);
        entity.setTitle(title);
        entity.setBody(body);
        entity.setLinkUrl(linkUrl);
        NotificationEntity saved = repository.save(entity);

        if (recipientEmail != null && !recipientEmail.isBlank()) {
            emailSender.sendHtml(recipientEmail, "[ERPMS] " + title, renderHtml(title, body, linkUrl));
        }
        return toResponse(saved);
    }

    /** Deliver a plain email without persisting a notification (used for OTP flows). */
    public void sendTransactionalEmail(String to, String subject, String htmlBody) {
        emailSender.sendHtml(to, subject, htmlBody);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> listForCurrentUser(int page, int size) {
        String userId = requireCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        return PageResponse.from(repository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public long unreadCountForCurrentUser() {
        return repository.countByRecipientUserIdAndReadFalse(requireCurrentUserId());
    }

    @Transactional
    public void markRead(String id) {
        NotificationEntity entity = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Notification", id));
        entity.setRead(true);
        repository.save(entity);
    }

    @Transactional
    public void markAllReadForCurrentUser() {
        String userId = requireCurrentUserId();
        repository.findByRecipientUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())
                .forEach(n -> {
                    if (!n.isRead()) {
                        n.setRead(true);
                        repository.save(n);
                    }
                });
    }

    private String requireCurrentUserId() {
        String userId = SecurityUtils.currentUserIdOrNull();
        if (userId == null) {
            throw new IllegalStateException("No authenticated user in current context");
        }
        return userId;
    }

    private NotificationResponse toResponse(NotificationEntity n) {
        return new NotificationResponse(
                n.getId(), n.getRecipientUserId(), n.getCategory(), n.getTitle(),
                n.getBody(), n.getLinkUrl(), n.isRead(), n.getCreatedAt()
        );
    }

    private String renderHtml(String title, String body, String linkUrl) {
        String link = linkUrl == null ? "" :
                "<p><a href=\"" + linkUrl + "\" style=\"color:#2563eb\">Open in ERPMS</a></p>";
        return """
                <div style="font-family:Inter,Arial,sans-serif;color:#0f172a">
                  <h2 style="color:#1e293b">%s</h2>
                  <p>%s</p>
                  %s
                  <hr/>
                  <small style="color:#64748b">You received this message from the ERPMS platform.</small>
                </div>
                """.formatted(escape(title), escape(body), link);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
