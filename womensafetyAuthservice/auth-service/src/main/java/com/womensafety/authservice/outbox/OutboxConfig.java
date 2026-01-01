package com.womensafety.authservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OutboxConfig {
    @Bean
    public OutboxFactory outboxFactory(ObjectMapper objectMapper) {
        return new OutboxFactory(objectMapper);
    }
}
