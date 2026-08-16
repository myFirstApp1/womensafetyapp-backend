package com.womensafety.sosservice.ai.decision;

import com.womensafety.sosservice.ai.dto.PredictionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProtectionDecisionResolver {

    public ProtectionDecision resolve(
            PredictionResponse response
    ) {

        if (response == null) {
            return ProtectionDecision.MONITOR;
        }

        if ("CRITICAL".equalsIgnoreCase(response.getDangerLevel())) {
            return ProtectionDecision.TRIGGER_SOS;
        }

        if ("HIGH".equalsIgnoreCase(response.getDangerLevel())) {
            return ProtectionDecision.SHOW_WARNING;
        }

        if ("WARNING".equalsIgnoreCase(response.getDangerLevel())) {
            return ProtectionDecision.MONITOR;
        }

        return ProtectionDecision.NO_ACTION;
    }
}