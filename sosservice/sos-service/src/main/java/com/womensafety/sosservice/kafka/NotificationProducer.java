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
import java.util.concurrent.TimeUnit;

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
    public void sendAutomaticSOS(
            UUID userId,
            String currentLocation,
            String channel
    ) {

        String sosTraceId = UUID.randomUUID().toString();

        try {
            MDC.put("sosTraceId", sosTraceId);

            log.info(
                    "🚨 Triggering SOS for userId={} at location={}",
                    userId,
                    currentLocation
            );

            // =========================================
            // FETCH EMERGENCY CONTACTS
            // =========================================

            List<String> contacts =
                    userServiceClient.getEmergencyContacts(userId.toString());

            if (contacts == null || contacts.isEmpty()) {

                log.error(
                        "SOS_NOTIFICATION_FAILED | No emergency contacts found | userId={}",
                        userId
                );

                throw new IllegalStateException(
                        "No emergency contacts found for userId=" + userId
                );
            }

            // =========================================
            // TIME
            // =========================================

            String timestamp =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "yyyy-MM-dd HH:mm:ss"
                                    )
                            );
            // =========================================
            // GOOGLE MAPS LINK
            // =========================================

            String mapsLink = "UNKNOWN";

            if (currentLocation != null &&
                    !currentLocation.equals("UNKNOWN")) {

                mapsLink =
                        "https://maps.google.com/?q="
                                + currentLocation;
            }

            // =========================================
            // MESSAGE
            // =========================================

            String message =
                    String.format(
                            "🚨 Emergency detected!\n" +
                                    "User might be in danger.\n\n" +
                                    "📍 Location:\n%s\n\n" +
                                    "🕒 Time: %s\n\n" +
                                    "Please check immediately.",
                            mapsLink,
                            timestamp
                    );

            if (mapsLink.equals("UNKNOWN")) {

                message +=
                        "\n⚠️ Location unavailable. " +
                                "Last known signal lost.";
            }

            String subject =
                    "SOS Alert – Immediate Attention Required";

            // =========================================
            // SEND TO ALL CONTACTS
            // =========================================

            for (String contactNumber : contacts) {

                List<NotificationRequest.Channel> channels =
                        List.of(
                                NotificationRequest.Channel.SMS,
                                NotificationRequest.Channel.VOICE
                        );

                for (NotificationRequest.Channel notificationChannel :
                        channels) {

                    NotificationRequest request =
                            new NotificationRequest(
                                    userId,
                                    notificationChannel,
                                    subject,
                                    message,
                                    null,
                                    Map.of("to", contactNumber),
                                    sosTraceId
                            );

                    // =========================================
                    // WAIT FOR KAFKA ACK
                    // =========================================

                    try {

                        kafkaTemplate
                                .send(
                                        SOS_TOPIC,
                                        request
                                )
                                .get(
                                        10,
                                        TimeUnit.SECONDS
                                );

                        log.info(
                                "[Channel={}] Kafka SOS published successfully | " +
                                        "contact={} userId={}",
                                notificationChannel,
                                contactNumber,
                                userId
                        );

                    } catch (Exception e) {

                        log.error(
                                "KAFKA_SEND_FAILED | channel={} " +
                                        "contact={} userId={}",
                                notificationChannel,
                                contactNumber,
                                userId,
                                e
                        );

                        throw new RuntimeException(
                                "Kafka SOS publish failed",
                                e
                        );
                    }
                }
            }

            log.info(
                    "SOS dispatch completed for userId={}",
                    userId
            );

        } finally {
            MDC.clear();
        }
    }
    /**
     * 🔥 DEAD LETTER QUEUE (DLT)
     */
    public void sendToDLT(
            UUID userId,
            String location
    ) throws Exception {

        NotificationRequest request = new NotificationRequest(
                userId,
                NotificationRequest.Channel.SMS,
                "DLT SOS",
                "FAILED EVENT: " + location,
                null,
                Map.of(
                        "userId", userId.toString(),
                        "location", location,
                        "source", "SOS_SERVICE"
                ),
                UUID.randomUUID().toString()
        );

        try {

            kafkaTemplate
                    .send(SOS_TOPIC_DLT, request)
                    .get(10, TimeUnit.SECONDS);

            log.info(
                    "DLT published successfully | userId={}",
                    userId
            );

        } catch (Exception e) {

            log.error(
                    "DLT_SEND_FAILED | userId={}",
                    userId,
                    e
            );

            throw e;
        }
    }
}