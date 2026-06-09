package com.womensafety.sosservice.service.risk;

import com.womensafety.sosservice.domain.*;

/**
 * Engine for evaluating multi-sensor risk signals and making SOS trigger decisions.
 * Combines tamper analysis, heartbeat loss, and GPS analysis into a unified risk score.
 */
public interface IRiskDecisionEngine {

    /**
     * Evaluate risk from multiple sensor inputs and determine if SOS should be triggered.
     *
     * @param tamper tamper/strap analysis result
     * @param heartbeat heartbeat loss analysis result
     * @param gps GPS location analysis result
     * @return risk decision result with final score, SOS trigger flag, and reason
     */
    RiskDecisionResult evaluate(
            TamperAnalysisResult tamper,
            HeartbeatLossResult heartbeat,
            GpsAnalysisResult gps
    );
}
