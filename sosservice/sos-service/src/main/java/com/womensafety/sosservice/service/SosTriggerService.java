package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.OutboxStatus;
import com.womensafety.sosservice.domain.SessionStatus;
import com.womensafety.sosservice.domain.SosOutbox;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import com.womensafety.sosservice.statemachine.SessionStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SosTriggerService {
    private final SosOutboxRepository sosOutboxRepository;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;

    private final SessionStateMachineService
            stateMachineService;

    private final EmergencyTimelineService
            emergencyTimelineService;

    public void triggerSosViaOutbox(
            ActiveSafetySession session,
            String triggerReason
    ) {

        String location = "UNKNOWN";

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
                triggerReason,
                location
        );

        if (Boolean.TRUE.equals(session.getEmergencyTriggered())) {

            log.warn(
                    "DUPLICATE_SOS_PREVENTED | userId={}",
                    session.getUserId()
            );

            return;
        }

        if (session.getTrackingId() == null) {

            session.setTrackingId(
                    UUID.randomUUID().toString()
            );
        }

        // =========================================
        // PREVENT DUPLICATE SOS
        // =========================================

        session.setEmergencyTriggered(true);
        session.setEmergencyContactNotified(false);
        emergencyTimelineService.recordEvent(
                session,
                "SOS_TRIGGERED",
                triggerReason +
                        " | mode=" +
                        session.getCommunicationMode()
        );
        activeSafetySessionRepository.save(session);
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
                triggerReason,
                "SOS_ENGINE"
        );

        activeSafetySessionRepository.save(session);
    }

}
