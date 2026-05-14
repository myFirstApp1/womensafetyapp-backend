package com.womensafety.notificationservice.consumer;

import com.tl.womensafety.common.events.EmailVerificationEvent;
import com.womensafety.notificationservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailVerificationConsumer {
    private final EmailService emailService;

    public EmailVerificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 3000, multiplier = 2),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "send.email.verification",
            groupId = "notification-service-group"
    )
    public void consume(EmailVerificationEvent event) {
        log.info("Received EmailVerificationEvent: {}", event);
        // TODO: Build verification link
        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + event.token();
        // TODO: Send email (use SMTP or mock for now)
        log.info("Verification email would be sent to {} with link: {}", event.email(), verificationLink);
        //log.info("MOCK EMAIL → To: {}, Subject: {}, Body: {}", event.email(), "Verify your account - Women Safety App", verificationLink);
        emailService.sendVerificationEmail(event.email(), verificationLink);
    }
}