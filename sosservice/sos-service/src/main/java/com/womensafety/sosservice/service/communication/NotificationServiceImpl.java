package com.womensafety.sosservice.service.communication;

import com.womensafety.sosservice.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationProducer producer;

    @Override
    public void sendAutomaticSos(
            UUID userId,
            String location,
            String channel
    ) throws Exception {

        try {

            if (channel != null &&
                    channel.equalsIgnoreCase("whatsapp")) {

                log.info(
                        "Routing SOS via WhatsApp for user={}",
                        userId
                );
            }

            producer.sendAutomaticSOS(
                    userId,
                    location,
                    channel
            );

        } catch (Exception e) {

            log.error(
                    "Failed to sendAutomaticSos user={}",
                    userId,
                    e
            );

            throw e;
        }
    }

    @Override
    public void sendToDLT(UUID userId, String location) throws Exception {
        try {
            producer.sendToDLT(userId, location);
        } catch (Exception e) {
            log.error("Failed to sendToDLT user={}", userId, e);
            throw e;
        }
    }
}
