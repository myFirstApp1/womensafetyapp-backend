package com.womensafety.sosservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class DLTConsumer {
    @KafkaListener(topics = "sos-topic-dlt", groupId = "notification-service-group")
    public void consume(String message) {
        log.error("DLT MESSAGE RECEIVED: {}", message);
    }
}
