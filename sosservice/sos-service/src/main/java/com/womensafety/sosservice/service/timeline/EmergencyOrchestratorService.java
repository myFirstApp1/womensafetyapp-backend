package com.womensafety.sosservice.service.timeline;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.service.risk.RiskDecisionEngine;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.emergency.EmergencyExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyOrchestratorService {

    private final RiskDecisionEngine
            riskDecisionEngine;

    private final EmergencyExecutionService
            emergencyExecutionService;

    private final SessionManager sessionManager;

    public void evaluateAndTrigger(

            ActiveSafetySession session,

            TamperAnalysisResult tamper,

            HeartbeatLossResult heartbeat,

            GpsAnalysisResult gps

    ) {

        RiskDecisionResult decision =
                riskDecisionEngine.evaluate(
                        tamper,
                        heartbeat,
                        gps
                );

        sessionManager.setRisk(
                session,
                decision.getFinalRiskScore()
        );

        if (!decision.isTriggerSos()) {

            log.info(
                    "NO_SOS_REQUIRED | userId={} | risk={}",
                    session.getUserId(),
                    decision.getFinalRiskScore()
            );

            return;
        }

        log.error(
                "AUTO_SOS_TRIGGERED | userId={} | risk={}",
                session.getUserId(),
                decision.getFinalRiskScore()
        );



        emergencyExecutionService.executeEmergency(
                session,
                decision.getFinalRiskScore(),
                decision.getReason(),
                "WEARABLE_AI"
        );
    }
}
