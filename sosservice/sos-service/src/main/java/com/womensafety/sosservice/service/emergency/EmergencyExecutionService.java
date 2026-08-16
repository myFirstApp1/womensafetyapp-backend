package com.womensafety.sosservice.service.emergency;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.domain.enums.IncidentTriggerType;
import com.womensafety.sosservice.dto.IncidentResponse;
import com.womensafety.sosservice.service.incident.IncidentService;
import com.womensafety.sosservice.service.sos.SosTriggerService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineOrchestratorService;
import com.womensafety.sosservice.service.tracking.TrackingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmergencyExecutionService {
    private final IncidentService incidentService;
    private final TrackingService trackingService;
    private final SosTriggerService sosTriggerService;
    private final EmergencyTimelineOrchestratorService timelineOrchestrator;

    public void executeEmergency(
            ActiveSafetySession session,
            Integer riskScore,
            String reason,
            String source
    ) {

        IncidentResponse incident =
                incidentService.createIncident(
                        session.getUserId(),
                        session.getTrackingId(),
                        IncidentTriggerType.AI,
                        riskScore,
                        session.getLastLatitude(),
                        session.getLastLongitude(),
                        source
                );

        Incident incidentEntity =
                incidentService.findById(
                        incident.incidentId()
                );

        incidentService.updateStatus(
                incident.incidentId(),
                IncidentStatus.TRACKING
        );

        trackingService.attachIncident(
                session.getTrackingId(),
                incident.incidentId()
        );

        sosTriggerService.triggerSosViaOutbox(
                session,
                reason
        );

        timelineOrchestrator.riskScoreCalculated(
                incidentEntity,
                riskScore
        );

        timelineOrchestrator.trackingStarted(
                incidentEntity
        );

        timelineOrchestrator.gpsAcquired(
                incidentEntity
        );

        timelineOrchestrator.familyNotified(
                incidentEntity
        );

        timelineOrchestrator.smsSent(
                incidentEntity
        );

        timelineOrchestrator.voiceCallSent(
                incidentEntity
        );

    }

}
