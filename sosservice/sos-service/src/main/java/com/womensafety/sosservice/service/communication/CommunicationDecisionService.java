package com.womensafety.sosservice.service.communication;

import com.womensafety.sosservice.communication.LoRaGateway;
import com.womensafety.sosservice.communication.PhoneBluetoothGateway;
import com.womensafety.sosservice.communication.RelayGateway;
import com.womensafety.sosservice.communication.SatelliteGateway;
import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.CommunicationMode;
import com.womensafety.sosservice.domain.CommunicationResults;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunicationDecisionService implements ICommunicationDecisionService {
    private final PhoneBluetoothGateway
            phoneGateway;

    private final RelayGateway
            relayGateway;

    private final LoRaGateway
            loRaGateway;

    private final SatelliteGateway
            satelliteGateway;

    private final ActiveSafetySessionRepository
            repository;
    private final CommunicationFallbackService
            communicationFallbackService;

    private final EmergencyTimelineService
            timelineService;
    private final SessionManager sessionManager;
    private static final int PHONE_RETRY = 3;

    private static final int RELAY_RETRY = 2;

    private static final int LORA_RETRY = 2;
    public CommunicationMode
    attemptCommunication(
            ActiveSafetySession session
    ) {
        CommunicationResults result =
                phoneGateway.send(session);

        if (result.isAcknowledged()) {

            session.setCommunicationMode(
                    CommunicationMode.PHONE_BLUETOOTH
            );
            log.info(
                    "COMMUNICATION_SUCCESS | mode={}",
                    CommunicationMode.PHONE_BLUETOOTH
            );

            sessionManager.save(session);

            return CommunicationMode.PHONE_BLUETOOTH;
        }
        handleFailure(session);
        CommunicationResults relayResult =
                relayGateway.send(session);

        if (relayResult.isAcknowledged()) {
            session.setCommunicationMode(
                    CommunicationMode.NEARBY_RELAY
            );
            log.info(
                    "COMMUNICATION_SUCCESS | mode={}",
                    CommunicationMode.NEARBY_RELAY
            );
            sessionManager.save(session);

            return CommunicationMode.NEARBY_RELAY;
        }
        handleFailure(session);
        CommunicationResults communicationResult =
                loRaGateway.send(session);

        if (communicationResult.isAcknowledged()) {
            session.setCommunicationMode(
                    CommunicationMode.LORA
            );
            log.info(
                    "COMMUNICATION_SUCCESS | mode={}",
                    CommunicationMode.LORA
            );
            sessionManager.save(session);

            return CommunicationMode.LORA;
        }
        handleFailure(session);
        CommunicationResults satelliteResult =
                satelliteGateway.send(session);

        if (satelliteResult.isAcknowledged()) {

            session.setCommunicationMode(
                    CommunicationMode.SATELLITE
            );
            log.info(
                    "COMMUNICATION_SUCCESS | mode={}",
                    CommunicationMode.SATELLITE
            );

            sessionManager.save(session);

            return CommunicationMode.SATELLITE;
        }
        handleFailure(session);
        return CommunicationMode.SATELLITE;
    }
    private void handleFailure(
            ActiveSafetySession session
    ) {

        int failureCount =
                session.getCommunicationFailureCount() == null
                        ? 0
                        : session.getCommunicationFailureCount();

        session.setCommunicationFailureCount(
                failureCount + 1
        );

        sessionManager.recordEvent(
                session,
                "COMMUNICATION_FAILED",
                session.getCommunicationMode() == null
                        ? "UNKNOWN"
                        : session.getCommunicationMode().name()
        );

        communicationFallbackService
                .escalateCommunication(
                        session
                );

        sessionManager.save(session);
    }

}
