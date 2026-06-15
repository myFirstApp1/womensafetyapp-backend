package com.womensafety.sosservice.service.sensor;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.RuleType;
import org.springframework.stereotype.Service;

@Service
public class AiSensorRulesService {

    public RuleEvaluationResult evaluate(

            int heartRate,

            int movementScore,

            boolean deviceWorn,

            boolean bluetoothConnected,

            boolean preAlertActive
    ) {

        // =====================================
        // EXTREME ATTACK
        // =====================================

        if (!deviceWorn
                && heartRate >= 140
                && movementScore >= 90) {

            return RuleEvaluationResult.builder()
                    .ruleType(
                            RuleType.EXTREME_ATTACK
                    )
                    .riskScore(100)
                    .triggerSos(true)
                    .reason(
                            "Device removed with extreme panic"
                    )
                    .build();
        }

        // =====================================
        // POSSIBLE ATTACK
        // =====================================

        if (!deviceWorn
                && movementScore >= 70) {

            return RuleEvaluationResult.builder()
                    .ruleType(
                            RuleType.POSSIBLE_ATTACK
                    )
                    .riskScore(80)
                    .triggerSos(true)
                    .reason(
                            "Violent removal detected"
                    )
                    .build();
        }

        // =====================================
        // PRE ALERT ESCALATION
        // =====================================

        if (preAlertActive
                && !bluetoothConnected) {

            return RuleEvaluationResult.builder()
                    .ruleType(
                            RuleType.PRE_ALERT_ESCALATION
                    )
                    .riskScore(70)
                    .triggerSos(true)
                    .reason(
                            "Pre-alert with signal loss"
                    )
                    .build();
        }

        // =====================================
        // SAFE
        // =====================================

        return RuleEvaluationResult.builder()
                .ruleType(
                        RuleType.SAFE_ACTIVITY
                )
                .riskScore(0)
                .triggerSos(false)
                .reason(
                        "Normal activity"
                )
                .build();
    }
}