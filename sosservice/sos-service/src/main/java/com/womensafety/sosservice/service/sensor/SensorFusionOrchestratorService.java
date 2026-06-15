package com.womensafety.sosservice.service.sensor;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.DangerAssessment;
import com.womensafety.sosservice.domain.SensorContext;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.sos.SosTriggerService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorFusionOrchestratorService {
    private final SensorFusionService sensorFusionService;

    private final SosTriggerService sosTriggerService;

    private final EmergencyTimelineService timelineService;
        private final SessionManager sessionManager;

    public void processFusion(
            ActiveSafetySession session,
            SensorContext context
    ) {

        DangerAssessment assessment =
                sensorFusionService.assess(
                        context
                );

        sessionManager.recordEvent(
                session,
                "SENSOR_FUSION",
                assessment.getReason()
        );

        if (Boolean.TRUE.equals(
                assessment.getAutoSos()
        )) {

            sosTriggerService.triggerSosViaOutbox(
                    session,
                    assessment.getReason()
            );
        }
    }
}
