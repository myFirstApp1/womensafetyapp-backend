package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentResponseService implements IIncidentResponseService {

        private final EmergencyTimelineService timelineService;

    private final LocationRecoveryService locationRecoveryService;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;

    private final SosTriggerService
            sosTriggerService;

    private final SessionManager sessionManager;

    public void processIncident(

            ActiveSafetySession session,

            String incidentType,

            Integer riskIncrease,

            boolean triggerSos
    ) {

        sessionManager.recordEvent(
                session,
                incidentType,
                "Incident detected"
        );
        locationRecoveryService.capture(
                session.getDeviceId(),
                incidentType
        );

        int currentRisk =
                session.getRiskScore() == null
                        ? 0
                        : session.getRiskScore();

        sessionManager.increaseRiskAndSave(session, riskIncrease);

        if (triggerSos) {

            sosTriggerService.triggerSosViaOutbox(
                    session,
                    incidentType
            );
        }

        log.info(
                "INCIDENT_PROCESSED | type={} | userId={} | risk={}",
                incidentType,
                session.getUserId(),
                session.getRiskScore()
        );
    }
}