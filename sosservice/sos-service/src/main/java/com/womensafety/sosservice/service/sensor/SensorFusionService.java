package com.womensafety.sosservice.service.sensor;

import com.womensafety.sosservice.ai.decision.ProtectionDecision;
import com.womensafety.sosservice.ai.dto.PredictionResponse;
import com.womensafety.sosservice.domain.DangerAssessment;
import com.womensafety.sosservice.domain.SensorContext;
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
        // PRE ALERT
        // =========================

        if (context.isPreAlertActive()) {
            risk += 20;
        }

        // =========================
        // HEARTBEAT LOSS
        // =========================

        if (context.getHeartbeatLoss() != null) {
            risk += context.getHeartbeatLoss().getRiskScore();
        }

        // =========================
        // TAMPER
        // =========================

        if (context.getTamperResult() != null) {
            risk += context.getTamperResult().getRiskScore();
        }

        // =========================
        // OFF BODY
        // =========================

        if (context.getOffBodyResult() != null) {
            risk += context.getOffBodyResult().getRiskScore();
        }

        // =========================
        // GPS
        // =========================

        if (context.getGpsResult() != null) {
            risk += context.getGpsResult().getRiskScore();
        }

        // =========================
        // AI Prediction
        // =========================

        PredictionResponse prediction =
                context.getPrediction();

        if (prediction != null) {

            risk = Math.max(
                    risk,
                    prediction.getRiskScore()
            );

        }

        // =========================
        // AI Decision Bonus
        // =========================

        ProtectionDecision decision =
                context.getAiDecision();

        if (decision != null) {

            switch (decision) {

                case TRIGGER_SOS -> {
                    risk += 30;
                    reason = "AI_TRIGGERED_SOS";
                }

                case SHOW_WARNING -> {
                    risk += 15;
                    reason = "AI_WARNING";
                }

                case MONITOR -> {
                    risk += 5;
                }

                case NO_ACTION -> {
                    // no additional risk
                }

            }

        }

        // =========================
        // Cap Risk
        // =========================

        risk = Math.min(risk, 100);

        // =========================
        // Final Danger Level
        // =========================

        DangerLevel level;

        if (risk >= 100) {

            level = DangerLevel.CRITICAL;

            if ("NORMAL".equals(reason)) {
                reason = "CRITICAL_DANGER";
            }

        } else if (risk >= 70) {

            level = DangerLevel.HIGH;

            if ("NORMAL".equals(reason)) {
                reason = "HIGH_RISK";
            }

        } else if (risk >= 40) {

            level = DangerLevel.MEDIUM;

            if ("NORMAL".equals(reason)) {
                reason = "MEDIUM_RISK";
            }

        } else if (risk >= 20) {

            level = DangerLevel.LOW;

            if ("NORMAL".equals(reason)) {
                reason = "LOW_RISK";
            }

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