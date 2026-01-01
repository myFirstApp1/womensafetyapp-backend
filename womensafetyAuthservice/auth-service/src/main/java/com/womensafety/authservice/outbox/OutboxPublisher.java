package com.womensafety.authservice.outbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.verification.topic:user.identity}")
    private String topic;

    @Value("${app.outbox.batch-size:50}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${app.outbox.poll-ms:2000}")
    @Transactional
    public void publishBatch() {
        List<OutboxEvent> batch = repository.fetchBatchForPublish(batchSize);
        if (batch.isEmpty()) return;

        log.debug("Publishing {} outbox events", batch.size());
        for (OutboxEvent evt : batch) {
            try {
                ProducerRecord<String, String> record =
                        new ProducerRecord<>(topic, evt.getAggregateId(), evt.getPayload());

                // Apply headers if present
                if (evt.getHeadersJson() != null) {
                    Map<String, String> headers = objectMapper.readValue(
                            evt.getHeadersJson(), new TypeReference<>() {});
                    headers.forEach((k, v) -> record.headers().add(k, v.getBytes()));
                }
                // Always include basic headers
                record.headers().add("event-type", evt.getEventType().getBytes());
                record.headers().add("event-version", "1".getBytes());
                record.headers().add(MessageHeaders.CONTENT_TYPE,
                        MimeTypeUtils.APPLICATION_JSON_VALUE.getBytes(StandardCharsets.UTF_8));

                kafkaTemplate.send(record).get(); // wait to ensure we can safely mark published
                evt.setPublishedAt(Instant.now());
                repository.save(evt);
                log.info("Published outbox event {} to topic {} key={}", evt.getEventId(), topic, evt.getAggregateId());
            } catch (Exception ex) {
                // Leave published_at = null → will retry next tick
                log.error("Failed to publish outbox event {}: {}", evt.getEventId(), ex.getMessage(), ex);
            }
        }
    }
}
