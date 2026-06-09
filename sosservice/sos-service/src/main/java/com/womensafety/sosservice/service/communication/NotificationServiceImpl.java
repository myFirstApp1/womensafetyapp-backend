package com.womensafety.sosservice.service.communication;

import com.womensafety.sosservice.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationProducer producer;

    @Override
    public void sendAutomaticSos(String userId, String location, String channel) throws Exception {
        // For now the existing producer handles channels internally; keep call simple
        try {
            if (channel != null && channel.equalsIgnoreCase("whatsapp")) {
                log.info("Routing SOS via WhatsApp for user={}", userId);
            }

            producer.sendAutomaticSOS(userId, location);
        } catch (Exception e) {
            log.error("Failed to sendAutomaticSos user={}", userId, e);
            // bubble up so controller can decide how to respond
            throw e;
        }
    }

    @Override
    public void sendToDLT(String userId, String location) throws Exception {
        try {
            producer.sendToDLT(userId, location);
        } catch (Exception e) {
            log.error("Failed to sendToDLT user={}", userId, e);
            throw e;
        }
    }
}
