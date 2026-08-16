package com.womensafety.sosservice.ai.service;

import com.womensafety.sosservice.ai.dto.PredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIConfidenceGateService {

    @Value("${ai.minimum-confidence:80}")
    private double minimumConfidence;

    public boolean shouldTrust(
            PredictionResponse prediction
    ) {

        if (prediction == null) {
            return false;
        }

        return prediction.getConfidence() >= minimumConfidence;
    }

}