package com.womensafety.notificationservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tl.womensafety.common.dto.NotificationRequest;
import com.womensafety.notificationservice.service.EmailService;
//import com.womensafety.notificationservice.service.WhatsAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EmailService emailService;
    //private final WhatsAppService whatsAppService;

    @KafkaListener(topics = "notifications.request", groupId = "notification-service-group")
    public void consumeNotificationRequest(NotificationRequest request) {
            System.out.println("Notification received: " + request);

            // For now just log
            // Later → Twilio/SMTP/Firebase handling
       /* } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
            throw new RuntimeException("Failed to process message", e);
        }*/
    }

    @KafkaListener(topics = "sos-topic", groupId = "notification-service-group")
    public void consumeSosRequest(NotificationRequest request) {
        try {
            //  Add traceId to MDC context
            if (request.traceId() != null) {
                MDC.put("sosTraceId", request.traceId());
            }
            log.info(" Received SOS Notification | userId={} | channel={} | traceId={}",
                    request.userId(), request.channel(), request.traceId());
        // Only handle EMAIL notifications for now
        if (request.channel() == NotificationRequest.Channel.EMAIL) {
            String to = request.metadata().get("to"); // expect "to" in metadata map
            String location = request.metadata().getOrDefault("location", "Unknown");
            String subject = request.subject() != null
                    ? request.subject()
                    : "SOS Alert from Women Safety App";
            String body = request.message() + "\nLocation: " + location;
            log.info("Sending SOS email to {}: {}", to, body);
            emailService.sendEmail(to, subject, body);
        }
        // WHATSAPP notifications
        /*else if (request.channel() == NotificationRequest.Channel.WHATSAPP) {
            String to = request.metadata().get("to");
            String message = request.message();
            log.info("Sending WhatsApp SOS alert to {} via Twilio: {}", to, message);
            whatsAppService.sendWhatsAppMessage(to, message);
            log.info(" Notification sent successfully | traceId={}", request.traceId());
        }*/
        } catch (Exception e) {
            log.error(" Error processing notification | traceId={} | error={}",
                    request.traceId(), e.getMessage());
        } finally {
            MDC.clear();
        }
    }
}
