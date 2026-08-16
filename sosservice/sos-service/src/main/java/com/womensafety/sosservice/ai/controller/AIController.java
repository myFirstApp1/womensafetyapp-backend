package com.womensafety.sosservice.ai.controller;

import com.womensafety.sosservice.ai.dto.HealthResponse;
import com.womensafety.sosservice.ai.dto.PredictionRequest;
import com.womensafety.sosservice.ai.dto.PredictionResponse;
import com.womensafety.sosservice.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "ai.protection.enabled",
        havingValue = "true"
)
public class AIController {

    private final AIService aiService;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
                aiService.health()
        );
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> predict(
            @RequestBody PredictionRequest request
    ) {
        return ResponseEntity.ok(
                aiService.predict(request)
        );
    }
}