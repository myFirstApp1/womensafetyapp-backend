package com.womensafety.sosservice.service.sos;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.CommunicationMode;
import com.womensafety.sosservice.domain.enums.OutboxStatus;
import com.womensafety.sosservice.domain.enums.SessionStatus;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.SosOutboxRepository;

import com.womensafety.sosservice.service.communication.CommunicationDecisionService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.statemachine.SessionStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SosTriggerService implements ISosTriggerService {
    private final SosOutboxRepository sosOutboxRepository;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;

    private final SessionStateMachineService
            stateMachineService;

    private final EmergencyTimelineService
            emergencyTimelineService;
    private final CommunicationDecisionService
            communicationDecisionService;
    private final SessionManager sessionManager;

    public void triggerSosViaOutbox(
            ActiveSafetySession session,
           // UUID incidentId,
            String reason

    ) {
        String location = "UNKNOWN";

        CommunicationMode mode =
                communicationDecisionService
                        .attemptCommunication(
                                session
                        );

        log.warn(
                "COMMUNICATION_SELECTED | trackingId={} | mode={}",
                session.getTrackingId(),
                mode
        );

        if (session.getLastLatitude() != null &&
                session.getLastLongitude() != null) {

            location =
                    session.getLastLatitude().toPlainString()
                            + ","
                            +
                            session.getLastLongitude().toPlainString();
        }

        log.error(
                "🚨 SOS_TRIGGERED | userId={} | reason={} | location={}",
                session.getUserId(),
                reason,
                location
        );

        if (Boolean.TRUE.equals(session.getEmergencyTriggered())) {

            log.warn(
                    "DUPLICATE_SOS_PREVENTED | userId={}",
                    session.getUserId()
            );

            return;
        }

        sessionManager.ensureTrackingId(session);

        // =========================================
        // PREVENT DUPLICATE SOS
        // =========================================

        session.setEmergencyTriggered(true);
        session.setEmergencyContactNotified(false);
        sessionManager.recordEvent(
                session,
                "SOS_TRIGGERED",
                reason +
                        " | mode=" +
                        session.getCommunicationMode()
        );
        sessionManager.save(session);
        // =========================================
        // CREATE OUTBOX EVENT
        // =========================================

        SosOutbox event = new SosOutbox();

        event.setEventId(
                UUID.randomUUID().toString()
        );

        event.setUserId(session.getUserId());
        event.setTrackingId(
                session.getTrackingId()
        );
        
       // event.setIncidentId(incidentId);

        event.setLocation(location);

        event.setStatus(OutboxStatus.PENDING);

        event.setRetryCount(0);

        sosOutboxRepository.save(event);

        // =========================================
        // STATE TRANSITION
        // =========================================

        stateMachineService.transitionState(
                session,
                SessionStatus.IN_DANGER,
                reason,
                "SOS_ENGINE"
        );

        sessionManager.save(session);
    }

}
