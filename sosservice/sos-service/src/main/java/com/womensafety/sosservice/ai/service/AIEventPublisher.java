package com.womensafety.sosservice.ai.service;

import com.womensafety.sosservice.domain.AIEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIEventPublisher {

    public void publish(
            AIEvent event
    ) {

        log.info(
                "AI_EVENT | user={} prediction={} danger={} risk={}",
                event.getUserId(),
                event.getPrediction(),
                event.getDangerLevel(),
                event.getRiskScore()
        );

    }

}