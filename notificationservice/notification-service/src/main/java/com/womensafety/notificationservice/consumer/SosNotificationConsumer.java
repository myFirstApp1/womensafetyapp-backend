package com.womensafety.notificationservice.consumer;

import com.tl.womensafety.common.dto.NotificationRequest;
import com.womensafety.notificationservice.service.TwilioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SosNotificationConsumer {

    private final TwilioService twilioService;

    @KafkaListener(topics = "sos-topic", groupId = "notification-service-group")
    public void consume(NotificationRequest request) {
        log.info("🔥 SOS MESSAGE RECEIVED: {}", request);
        String phone = request.metadata().get("to");
        String message = request.message();

        try {
            switch (request.channel()) {

                case SMS -> {
                    twilioService.sendSMS(phone, message);
                }

                case VOICE -> {
                    twilioService.makeCall(phone, message);
                }

                default -> log.warn("Unsupported channel {}", request.channel());
            }

        } catch (Exception e) {
            log.error("Failed to send notification to {}", phone, e);
            // optional: send to DLT or retry queue
        }
    }
}