package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmergencyCommunicationService {

    public CommunicationResult attemptCommunication(
            ActiveSafetySession session
    ) {
        CommunicationMode mode =
                session.getCommunicationMode();
        if (mode == null) {
            log.warn(
                    "COMMUNICATION_MODE_NULL | userId={} | defaulting to PHONE_BLUETOOTH",
                    session.getUserId()
            );
            mode = CommunicationMode.PHONE_BLUETOOTH;
            session.setCommunicationMode(mode);
        }
        return switch (mode) {
            case PHONE_BLUETOOTH -> handlePhoneBluetooth(session);
            case NEARBY_RELAY -> handleNearbyRelay(session);
            case LORA -> handleLora(session);
            case SATELLITE -> handleSatellite(session);
            default -> CommunicationResult.FAILED;
        };
    }

    // =========================================
    // PHONE BLUETOOTH
    // =========================================

    private CommunicationResult handlePhoneBluetooth(
            ActiveSafetySession session
    ) {

        log.warn(
                "📡 TRYING_PHONE_BLUETOOTH | userId={}",
                session.getUserId()
        );

        boolean success = false;

        if (success) {

            log.info(
                    "✅ PHONE_BLUETOOTH_SUCCESS | userId={}",
                    session.getUserId()
            );

            notifyEmergencyContacts(session);

            notifyNearbyPolice(session);

            return CommunicationResult.SUCCESS;
        }

        log.error(
                "❌ PHONE_BLUETOOTH_FAILED | userId={}",
                session.getUserId()
        );

        return CommunicationResult.FAILED;
    }

    // =========================================
    // NEARBY RELAY
    // =========================================

    private CommunicationResult handleNearbyRelay(
            ActiveSafetySession session
    ) {

        log.warn(
                "📡 TRYING_NEARBY_RELAY | userId={}",
                session.getUserId()
        );

        boolean success = false;

        if (success) {

            log.info(
                    "✅ NEARBY_RELAY_SUCCESS | userId={}",
                    session.getUserId()
            );

            notifyEmergencyContacts(session);

            notifyNearbyPolice(session);

            return CommunicationResult.SUCCESS;
        }

        log.error(
                "❌ NEARBY_RELAY_FAILED | userId={}",
                session.getUserId()
        );

        return CommunicationResult.FAILED;
    }

    // =========================================
    // LORA
    // =========================================

    private CommunicationResult handleLora(
            ActiveSafetySession session
    ) {

        log.warn(
                "📡 TRYING_LORA | userId={}",
                session.getUserId()
        );

        boolean success = false;

        if (success) {

            log.info(
                    "✅ LORA_SUCCESS | userId={}",
                    session.getUserId()
            );

            notifyEmergencyContacts(session);

            notifyNearbyPolice(session);

            return CommunicationResult.SUCCESS;
        }

        log.error(
                "❌ LORA_FAILED | userId={}",
                session.getUserId()
        );

        return CommunicationResult.FAILED;
    }

    // =========================================
    // SATELLITE
    // =========================================

    private CommunicationResult handleSatellite(
            ActiveSafetySession session
    ) {

        log.warn(
                "🛰️ TRYING_SATELLITE | userId={}",
                session.getUserId()
        );

        log.error(
                "🛰️ SATELLITE_NOT_IMPLEMENTED_YET | userId={}",
                session.getUserId()
        );

        return CommunicationResult.FAILED;
    }

    // =========================================
    // HELPERS
    // =========================================

    private void notifyEmergencyContacts(
            ActiveSafetySession session
    ) {

        log.warn(
                "👨‍👩‍👧 FAMILY_ALERT_SENT | userId={}",
                session.getUserId()
        );
    }

    private void notifyNearbyPolice(
            ActiveSafetySession session
    ) {

        log.warn(
                "🚔 POLICE_ALERT_SENT | userId={}",
                session.getUserId()
        );
    }
}