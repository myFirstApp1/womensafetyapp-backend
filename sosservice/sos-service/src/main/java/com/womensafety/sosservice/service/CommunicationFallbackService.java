package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.CommunicationMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class CommunicationFallbackService {

    public void escalateCommunication(
            ActiveSafetySession session
    ) {

        CommunicationMode currentMode =
                session.getCommunicationMode();

        if (currentMode == null) {

            currentMode = CommunicationMode.PHONE_BLUETOOTH;

            session.setCommunicationMode(currentMode);
        }

        switch (currentMode) {

            case PHONE_BLUETOOTH:

                log.warn(
                        "PHONE_FAILED -> SWITCHING_TO_NEARBY_RELAY | userId={}",
                        session.getUserId()
                );

                session.setCommunicationMode(
                        CommunicationMode.NEARBY_RELAY
                );

                break;

            case NEARBY_RELAY:

                log.warn(
                        "RELAY_FAILED -> SWITCHING_TO_LORA | userId={}",
                        session.getUserId()
                );

                session.setCommunicationMode(
                        CommunicationMode.LORA
                );

                break;

            case LORA:

                log.error(
                        "LORA_FAILED -> SWITCHING_TO_SATELLITE | userId={}",
                        session.getUserId()
                );

                session.setCommunicationMode(
                        CommunicationMode.SATELLITE
                );

                break;

            case SATELLITE:

                log.error(
                        "ALL_COMMUNICATION_FAILED | userId={}",
                        session.getUserId()
                );

                break;
        }

        session.setLastCommunicationAttempt(
                LocalDateTime.now()
        );

        int currentFailureCount =
                session.getCommunicationFailureCount() == null
                        ? 0
                        : session.getCommunicationFailureCount();

        session.setCommunicationFailureCount(
                currentFailureCount + 1
        );    }
}