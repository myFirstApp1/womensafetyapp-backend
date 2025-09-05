package com.womensafety.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womensafety.notificationservice.model.NotificationRequest;
import com.womensafety.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService;

    @KafkaListener(topics = "notifications.request", groupId = "notification-service-group")
    public void consume(String message) {
        try {
            NotificationRequest request = objectMapper.readValue(message, NotificationRequest.class);
            System.out.println("Notification received: " + request);

            // For now just log
            // Later → Twilio/SMTP/Firebase handling
        } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
            throw new RuntimeException("Failed to process message", e);
        }
    }

    @KafkaListener(topics = "sos-topic", groupId = "notification-group")
    public void consume(NotificationRequest request) {
        // Example: send to all emergency contacts
        request.getContacts().forEach(contact -> {
            if (contact.getPreferredChannels().contains("EMAIL")) {
                emailService.sendEmail(
                        contact.getEmail(),
                        "SOS Alert: " + request.getUser().getName(),
                        request.getMessage() + "\nLocation: " + request.getLocation().getAddress()
                );
            }
        });
    }
}
