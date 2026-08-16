package com.womensafety.sosservice.service.sensor;

import com.womensafety.sosservice.ai.service.AIEventPublisher;
import com.womensafety.sosservice.domain.AIEvent;
import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.DangerAssessment;
import com.womensafety.sosservice.domain.SensorContext;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.sos.SosTriggerService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorFusionOrchestratorService {
    private final SensorFusionService sensorFusionService;

    private final SosTriggerService sosTriggerService;

    private final EmergencyTimelineService timelineService;
        private final SessionManager sessionManager;
        private final AIEventPublisher aiEventPublisher;

    public void processFusion(
            ActiveSafetySession session,
            SensorContext context
    ) {

        DangerAssessment assessment =
                sensorFusionService.assess(
                        context
                );
        session.setRiskScore(
                assessment.getRiskScore()
        );

        aiEventPublisher.publish(
                AIEvent.builder()
                        .userId(session.getUserId())
                        .prediction(
                                context.getPrediction() != null
                                        ? context.getPrediction().getPrediction()
                                        : "UNKNOWN"
                        )
                        .dangerLevel(
                                assessment.getDangerLevel().name()
                        )
                        .riskScore(
                                assessment.getRiskScore()
                        )
                        .reason(
                                assessment.getReason()
                        )
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        sessionManager.recordEvent(
                session,
                "SENSOR_FUSION",
                assessment.getReason()
        );

        sessionManager.save(session);

        if (Boolean.TRUE.equals(
                assessment.getAutoSos()
        )) {

            log.warn(
                    "AUTO_SOS_TRIGGERED | risk={} | reason={}",
                    assessment.getRiskScore(),
                    assessment.getReason()
            );

            sosTriggerService.triggerSosViaOutbox(
                    session,
                    assessment.getReason()
            );
        }
    }
}
