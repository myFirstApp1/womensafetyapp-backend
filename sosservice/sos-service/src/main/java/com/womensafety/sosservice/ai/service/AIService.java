package com.womensafety.sosservice.ai.service;

import com.womensafety.sosservice.ai.client.AIClient;
import com.womensafety.sosservice.ai.dto.HealthResponse;
import com.womensafety.sosservice.ai.dto.PredictionRequest;
import com.womensafety.sosservice.ai.dto.PredictionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "ai.protection.enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
public class AIService {

    private final AIClient aiClient;
    @CircuitBreaker(
            name = "aiService",
            fallbackMethod = "fallbackPrediction"
    )
    public PredictionResponse predict(
            PredictionRequest request
    ) {

        try {

            return aiClient.predict(request);

        } catch (Exception ex) {

            log.warn("Retrying AI prediction...");

            try {

                return aiClient.predict(request);

            } catch (Exception retryEx) {

                log.error(
                        "AI unavailable.",
                        retryEx
                );

                return null;

            }

        }

    }



    public boolean isHealthy() {

        try {

            HealthResponse response = aiClient.health();

            return response != null
                    && "UP".equalsIgnoreCase(response.getStatus());

        } catch (Exception ex) {

            log.warn("Adhira AI is unavailable.");

            return false;
        }

    }
    public PredictionResponse fallbackPrediction(
            PredictionRequest request,
            Exception ex
    ) {

        log.warn(
                "AI Circuit Breaker activated. Falling back to Java protection engine.",
                ex
        );

        return null;

    }
    public HealthResponse health(){
        return null;
    }



}