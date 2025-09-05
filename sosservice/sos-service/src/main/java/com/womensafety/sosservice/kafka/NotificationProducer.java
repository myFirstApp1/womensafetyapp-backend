package com.womensafety.sosservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.womensafety.sosservice.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "notifications.request";

    public void sendNotification(NotificationRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(TOPIC, message);
            System.out.println("Notification sent: " + message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize NotificationRequest", e);
        }
    }
}