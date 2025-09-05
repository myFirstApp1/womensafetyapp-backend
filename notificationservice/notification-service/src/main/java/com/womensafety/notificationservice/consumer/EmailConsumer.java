package com.womensafety.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womensafety.notificationservice.dto.EmailRequest;
import com.womensafety.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notifications.request", groupId = "notification-service")
    public void consume(String message) {
        try {
            log.info("Received message from Kafka: {}", message);

            EmailRequest emailRequest = objectMapper.readValue(message, EmailRequest.class);

            emailService.sendEmail(
                    emailRequest.getTo(),
                    emailRequest.getSubject(),
                    emailRequest.getBody()
            );

            log.info("Email sent successfully to {}", emailRequest.getTo());

        } catch (Exception e) {
            log.error("Failed to process Kafka message: {}", message, e);
        }
    }
}