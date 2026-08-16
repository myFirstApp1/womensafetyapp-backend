package com.womensafety.sosservice.ai.service;

import com.womensafety.sosservice.ai.dto.PredictionResponse;
import com.womensafety.sosservice.ai.entity.AIPrediction;
import com.womensafety.sosservice.ai.repository.AIPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AIHistoryService {

    private final AIPredictionRepository repository;

    public void save(
            UUID userId,
            PredictionResponse response
    ) {

        repository.save(

                AIPrediction.builder()

                        .userId(userId)

                        .requestId(response.getRequestId())

                        .prediction(response.getPrediction())

                        .confidence(response.getConfidence())

                        .riskScore(response.getRiskScore())

                        .riskLevel(response.getRiskLevel())

                        .dangerLevel(response.getDangerLevel())

                        .recommendedAction(response.getRecommendedAction())

                        .modelVersion(response.getModelVersion())

                        .createdAt(LocalDateTime.now())

                        .build()

        );

    }

}