package com.womensafety.sosservice.ai.client;

import com.womensafety.sosservice.ai.dto.HealthResponse;
import com.womensafety.sosservice.ai.dto.PredictionRequest;
import com.womensafety.sosservice.ai.dto.PredictionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(
        name = "ai.protection.enabled",
        havingValue = "true"
)
@RequiredArgsConstructor
public class AIClient {

    private final RestClient restClient;

    @Value("${ai.base-url}")
    private String baseUrl;

    public PredictionResponse predict(PredictionRequest request) {

        return restClient.post()
                .uri(baseUrl + "/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PredictionResponse.class);
    }

    public HealthResponse health() {

        return restClient.get()
                .uri(baseUrl + "/health")
                .retrieve()
                .body(HealthResponse.class);
    }
}