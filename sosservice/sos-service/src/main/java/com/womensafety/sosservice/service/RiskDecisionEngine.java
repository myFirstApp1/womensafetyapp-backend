package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.GpsAnalysisResult;
import com.womensafety.sosservice.domain.HeartbeatLossResult;
import com.womensafety.sosservice.domain.RiskDecisionResult;
import com.womensafety.sosservice.domain.TamperAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RiskDecisionEngine {

    private static final int SOS_THRESHOLD = 100;

    public RiskDecisionResult evaluate(

            TamperAnalysisResult tamper,

            HeartbeatLossResult heartbeat,

            GpsAnalysisResult gps
    ) {

        int totalRisk = 0;

        StringBuilder reason =
                new StringBuilder();

        if (tamper != null) {

            totalRisk += tamper.getRiskScore();

            reason.append(
                    "Tamper="
                            + tamper.getTamperType()
                            + "; "
            );
        }

        if (heartbeat != null) {

            totalRisk += heartbeat.getRiskScore();

            reason.append(
                    "Heartbeat="
                            + heartbeat.getType()
                            + "; "
            );
        }

        if (gps != null) {

            totalRisk += gps.getRiskScore();

            reason.append(
                    "GPS="
                            + gps.getStatus()
                            + "; "
            );
        }

        boolean triggerSos =
                totalRisk >= SOS_THRESHOLD;

        log.info(
                "RISK_DECISION | risk={} | sos={}",
                totalRisk,
                triggerSos
        );

        return new RiskDecisionResult(
                totalRisk,
                triggerSos,
                reason.toString()
        );
    }
}