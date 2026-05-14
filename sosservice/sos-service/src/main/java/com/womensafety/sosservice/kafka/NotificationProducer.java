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
    private static final String SOS_TOPIC_DLT = "sos-topic-dlt";

    /**
     * 🚨 MAIN SOS TRIGGER METHOD
     */
    public void sendAutomaticSOS(String userId, String currentLocation) {

        String sosTraceId = UUID.randomUUID().toString();

        try {
            MDC.put("sosTraceId", sosTraceId);

            log.info("🚨 Triggering SOS for userId={} at location={}", userId, currentLocation);

            // 🔹 Fetch emergency contacts
            List<String> contacts = userServiceClient.getEmergencyContacts(userId);

            if (contacts == null || contacts.isEmpty()) {
                log.warn("⚠️ No emergency contacts found for userId={}", userId);
                return;
            }

            // 🔹 Time
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 🔹 Google Maps link
            String mapsLink = "UNKNOWN";

            if (currentLocation != null && !currentLocation.equals("UNKNOWN")) {
                mapsLink = "https://maps.google.com/?q=" + currentLocation;
            }

            // 🔹 Message
            String message = String.format(
                    "🚨 Emergency detected!\n" +
                            "User might be in danger.\n\n" +
                            "📍 Location:\n%s\n\n" +
                            "🕒 Time: %s\n\n" +
                            "Please check immediately.",
                    mapsLink,
                    timestamp
            );

            if (mapsLink.equals("UNKNOWN")) {
                message += "\n⚠️ Location unavailable. Last known signal lost.";
            }

            String subject = "SOS Alert – Immediate Attention Required";

            // 🔹 Send to all contacts + all channels
            for (String contactNumber : contacts) {
                List<NotificationRequest.Channel> channels = List.of(
                        NotificationRequest.Channel.SMS,
                        NotificationRequest.Channel.VOICE
                );

                for (NotificationRequest.Channel channel : channels) {

                    NotificationRequest request = new NotificationRequest(
                            UUID.fromString(userId),
                            channel,
                            subject,
                            message,
                            null,
                            Map.of("to", contactNumber),
                            sosTraceId
                    );

                    // ASYNC SEND (BEST PRACTICE)
                    kafkaTemplate.send(SOS_TOPIC, request)
                            .whenComplete((result, ex) -> {
                                if (ex != null) {
                                    log.error("Kafka send failed for userId={} contact={}", userId, contactNumber, ex);
                                } else {
                                    log.info("[Channel={}] SOS sent to contact={} userId={}",
                                            channel, contactNumber, userId);
                                }
                            });
                }
            }

            log.info("SOS dispatch completed for userId={}", userId);

        } finally {
            MDC.clear();
        }
    }

    /**
     * 🔥 DEAD LETTER QUEUE (DLT)
     */
    public void sendToDLT(String userId, String location) {

        NotificationRequest request = new NotificationRequest(
                UUID.fromString(userId),
                NotificationRequest.Channel.SMS,
                "DLT SOS",
                "FAILED EVENT: " + location,
                null,
                Map.of(
                        "userId", userId,
                        "location", location,
                        "source", "SOS_SERVICE"
                ),
                UUID.randomUUID().toString()
        );

        kafkaTemplate.send(SOS_TOPIC_DLT, request)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send to DLT for userId={}", userId, ex);
                    } else {
                        log.info("Sent to DLT for userId={}", userId);
                    }
                });
    }
}