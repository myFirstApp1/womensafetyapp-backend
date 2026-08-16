package com.womensafety.sosservice.service.risk;

import com.womensafety.sosservice.ai.dto.PredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIRiskFusionService {

    public int fuseRiskScore(
            int javaRiskScore,
            PredictionResponse prediction
    ) {

        if (prediction == null) {
            return javaRiskScore;
        }

        int aiRisk = prediction.getRiskScore();

        return Math.max(javaRiskScore, aiRisk);
    }

}