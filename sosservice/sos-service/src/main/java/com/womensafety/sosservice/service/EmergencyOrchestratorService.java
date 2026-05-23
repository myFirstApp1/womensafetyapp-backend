package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyOrchestratorService {

    private final RiskDecisionEngine
            riskDecisionEngine;

    private final SosTriggerService
            sosService;

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

        session.setRiskScore(
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

        sosService.triggerSosViaOutbox(
                session,
                decision.getReason()
        );
    }
}