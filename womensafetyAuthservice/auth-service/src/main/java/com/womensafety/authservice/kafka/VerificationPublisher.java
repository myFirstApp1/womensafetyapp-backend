/*
package com.womensafety.authservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificationPublisher {
    private final KafkaTemplate<String, String> kafka;
    @Value("${app.verification.topic}") String topic;

    public void publish(String userId, String email) {
        String payload = """
      {"userId":"%s","email":"%s"}
      """.formatted(userId, email);
        kafka.send(topic, userId, payload);
    }
}*/
