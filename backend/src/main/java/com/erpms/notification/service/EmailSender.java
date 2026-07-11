package com.erpms.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * SMTP transport used by the {@link NotificationService} for email delivery.
 *
 * <p>The bean gracefully degrades when {@code spring.mail.host} is not
 * configured: emails are logged to the console instead of being sent, which is
 * exactly what you want on developer laptops and CI runners.
 */
@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final boolean enabled;

    public EmailSender(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${erpms.mail.from:no-reply@erpms.local}") String fromAddress,
            @Value("${erpms.mail.enabled:false}") boolean enabled
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.enabled = enabled;
    }

    /**
     * Deliver an email asynchronously. When SMTP is not configured or delivery
     * is disabled, the payload is logged so integration tests and local
     * development remain frictionless.
     */
    @Async("erpmsTaskExecutor")
    public void sendHtml(String to, String subject, String htmlBody) {
        if (!enabled) {
            log.info("[mail:disabled] to='{}' subject='{}' body-length={}", to, subject,
                    htmlBody == null ? 0 : htmlBody.length());
            return;
        }
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) {
            log.warn("[mail:no-transport] to='{}' subject='{}' (JavaMailSender bean not present)", to, subject);
            return;
        }
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            sender.send(message);
            log.info("[mail:sent] to='{}' subject='{}'", to, subject);
        } catch (MessagingException ex) {
            log.error("[mail:failed] to='{}' subject='{}' error='{}'", to, subject, ex.getMessage(), ex);
        }
    }
}
