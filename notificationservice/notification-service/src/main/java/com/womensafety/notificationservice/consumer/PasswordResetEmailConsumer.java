package com.womensafety.notificationservice.consumer;

import com.tl.womensafety.common.events.PasswordResetEvent;
import com.womensafety.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasswordResetEmailConsumer {
    private final EmailService emailService;

    public PasswordResetEmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 3000, multiplier = 2),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "${app.email.password-reset.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handlePasswordReset(PasswordResetEvent event) {

        log.info("Received PasswordResetEvent for email: {}", event.getEmail());

        // 1️⃣ Build reset link
        String resetLink =
                "http://localhost:8080/reset-password?token=" + event.getToken();

        // 2️⃣ Send (mock) email

        emailService.sendEmail(
                event.getEmail(),
                "Reset your password – Women Safety App",
                "Click this link to reset your password:\n" + resetLink
        );

        log.info("Password reset email triggered for {}", event.getEmail());

    }
}
