package com.womensafety.sosservice.service.core;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import com.womensafety.sosservice.statemachine.SessionStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionManager {

    private final ActiveSafetySessionRepository sessionRepository;

    private final EmergencyTimelineService timelineService;

    private final SessionStateMachineService stateMachineService;

    public void recordEvent(ActiveSafetySession session, String eventType, String eventData) {
        timelineService.recordEvent(session, eventType, eventData);
    }

    public void recordEventByTrackingId(String trackingId, String eventType, String eventData) {
        timelineService.recordEvent(trackingId, eventType, eventData);
    }

    public void increaseRiskAndSave(ActiveSafetySession session, int delta) {
        int currentRisk = session.getRiskScore() == null ? 0 : session.getRiskScore();
        session.setRiskScore(currentRisk + delta);
        sessionRepository.save(session);
    }

    public void setRisk(ActiveSafetySession session, int risk) {
        session.setRiskScore(risk);
    }

    public void ensureTrackingId(
            ActiveSafetySession session) {

        log.error(
                "ENSURE_TRACKING_ID CALLED | before={}",
                session.getTrackingId()
        );

        if (session.getTrackingId() == null) {

            String trackingId =
                    UUID.randomUUID().toString();

            session.setTrackingId(trackingId);

            log.error(
                    "TRACKING_ID GENERATED = {}",
                    trackingId
            );
        }
    }

    public void save(ActiveSafetySession session) {
        sessionRepository.save(session);
    }

    public ActiveSafetySession saveAndReturn(ActiveSafetySession session) {
        return sessionRepository.save(session);
    }
}
