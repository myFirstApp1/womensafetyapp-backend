package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.DangerLevel;
import org.springframework.stereotype.Service;

@Service
public class SensorFusionService {

    public DangerAssessment assess(
            SensorContext context
    ) {

        int risk = 0;

        String reason = "NORMAL";

        // =========================
        // PRE ALERT BONUS
        // =========================

        if (context.isPreAlertActive()) {

            risk += 20;
        }

        // =========================
        // HEARTBEAT LOSS
        // =========================

        if (context.getHeartbeatLoss() != null) {

            risk += context
                    .getHeartbeatLoss()
                    .getRiskScore();
        }

        // =========================
        // TAMPER
        // =========================

        if (context.getTamperResult() != null) {

            risk += context
                    .getTamperResult()
                    .getRiskScore();
        }

        // =========================
        // OFF BODY
        // =========================

        if (context.getOffBodyResult() != null) {

            risk += context
                    .getOffBodyResult()
                    .getRiskScore();
        }

        // =========================
        // GPS
        // =========================

        if (context.getGpsResult() != null) {

            risk += context
                    .getGpsResult()
                    .getRiskScore();
        }

        // =========================
        // DETERMINE LEVEL
        // =========================

        DangerLevel level;

        if (risk >= 100) {

            level = DangerLevel.CRITICAL;

            reason = "CRITICAL_DANGER";

        } else if (risk >= 70) {

            level = DangerLevel.HIGH;

            reason = "HIGH_RISK";

        } else if (risk >= 40) {

            level = DangerLevel.MEDIUM;

            reason = "MEDIUM_RISK";

        } else if (risk >= 20) {

            level = DangerLevel.LOW;

            reason = "LOW_RISK";

        } else {

            level = DangerLevel.SAFE;
        }

        return DangerAssessment.builder()
                .riskScore(risk)
                .dangerLevel(level)
                .autoSos(risk >= 100)
                .reason(reason)
                .build();
    }
}