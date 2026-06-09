package com.womensafety.sosservice.service.risk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RiskScoreCalculatorService {

    public int calculateRiskScore(
            Integer heartRate,
            Integer movementScore,
            Boolean bluetoothConnected,
            Boolean deviceWorn
    ) {

        int risk = 0;

        // =====================================
        // HEART RATE
        // =====================================

        if (heartRate != null) {

            if (heartRate >= 150) {
                risk += 40;
            } else if (heartRate >= 120) {
                risk += 20;
            }
        }

        // =====================================
        // MOVEMENT
        // =====================================

        if (movementScore != null) {

            if (movementScore >= 90) {
                risk += 35;
            } else if (movementScore >= 70) {
                risk += 20;
            }
        }

        // =====================================
        // BLUETOOTH LOST
        // =====================================

        if (!bluetoothConnected) {
            risk += 10;
        }

        // =====================================
        // DEVICE REMOVED
        // =====================================

        if (!deviceWorn) {
            risk += 30;
        }

        log.warn("RISK_SCORE_CALCULATED | risk={}", risk);

        return risk;
    }
}
