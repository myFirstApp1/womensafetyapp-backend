package com.womensafety.authservice.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class OutboxFactory {

    private final ObjectMapper objectMapper;

    public OutboxFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEvent build(String aggregateType,
                             String aggregateId,
                             String eventType,
                             String eventId,
                             Object payloadObj,
                             Map<String, String> headers) {
        try {
            String payload = objectMapper.writeValueAsString(payloadObj);
            String headersJson = headers == null ? null : objectMapper.writeValueAsString(headers);
            return OutboxEvent.builder()
                    .eventId(eventId)
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .headersJson(headersJson)
                    .build();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox payload/headers", e);
        }
    }
}