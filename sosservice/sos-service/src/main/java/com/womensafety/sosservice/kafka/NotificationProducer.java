package com.womensafety.sosservice.kafka;

import com.tl.womensafety.common.dto.NotificationRequest;
import com.womensafety.sosservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationRequest> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    private static final String SOS_TOPIC = "sos-topic";

    public void sendAutomaticSOS(String userId, String currentLocation) {
        log.info(" Triggering SOS for userId={} at location={}", userId, currentLocation);
        String sosTraceId = UUID.randomUUID().toString();
        MDC.put("sosTraceId", sosTraceId);

        List<String> contacts = userServiceClient.getEmergencyContacts(userId);
        if (contacts.isEmpty()) {
            log.warn(" No emergency contacts found for userId={}", userId);
            return;
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message = String.format(
                " Emergency detected!\nUser might be in danger.\n Location: %s\n Time: %s\nPlease check immediately.",
                currentLocation, timestamp
        );
        String subject = " SOS Alert – Immediate Attention Required";

        for (String contactNumber : contacts) {
            for (NotificationRequest.Channel channel : NotificationRequest.Channel.values()) {
                NotificationRequest request = new NotificationRequest(
                        UUID.fromString(userId),
                        channel,
                        subject,
                        message,
                        null,
                        Map.of("to", contactNumber),sosTraceId
                );

                kafkaTemplate.send(SOS_TOPIC, request);
                log.info(" [Channel={}] SOS alert sent to Kafka for contact={} userId={}",
                        channel, contactNumber, userId);
            }
        }

        log.info(" SOS dispatch completed successfully for userId={}", userId);
        MDC.clear();
    }
}