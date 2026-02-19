package com.bluetide.services.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails. Sends asynchronously to avoid blocking the caller.
 * If SMTP is not configured (MAIL_USERNAME is empty), emails are logged instead of sent.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send a plain-text email asynchronously.
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("SMTP not configured – email NOT sent. to={}, subject={}", to, subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to={} subject={}: {}", to, subject, e.getMessage());
        }
    }

    /**
     * Send a notification email (convenience wrapper).
     */
    @Async
    public void sendNotificationEmail(String to, String notificationType, String message) {
        String subject = "BlueTide – " + notificationType;
        sendEmail(to, subject, message);
    }
}

