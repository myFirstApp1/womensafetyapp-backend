package com.womensafety.sosservice.service.risk;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.TamperEventType;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.incident.IncidentResponseService;
import com.womensafety.sosservice.service.location.LocationRecoveryService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TamperDetectionService {
    private final LocationRecoveryService
            locationRecoveryService;
    private final IncidentResponseService
            incidentResponseService;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;
    private final EmergencyTimelineService emergencyTimelineService;
    public TamperAnalysisResult analyze(
            String deviceId,
            boolean strapCut,
            boolean deviceOpened,
            boolean sensorDisabled,
            boolean firmwareModified,
            boolean bluetoothJammed,
            boolean gpsJammed,
            boolean powerDisconnected,
            boolean deviceDestroyed
    ) {

        if (deviceDestroyed) {

            log.error("DEVICE_DESTROYED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "DEVICE_DESTROYED",
                        100,
                        true
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.DEVICE_DESTROYED,
                    100,
                    true
            );
        }
        if (strapCut) {

            log.error("STRAP_CUT");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "STRAP_CUT",
                        100,
                        true
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.STRAP_CUT,
                    100,
                    true
            );
        }

        if (deviceOpened) {

            log.error("DEVICE_OPENED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "DEVICE_OPENED",
                        90,
                        true
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.DEVICE_OPENED,
                    90,
                    true
            );
        }

        if (firmwareModified) {

            log.error("FIRMWARE_MODIFIED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "FIRMWARE_MODIFIED",
                        90,
                        true
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.FIRMWARE_MODIFIED,
                    90,
                    true
            );
        }

        if (sensorDisabled) {

            log.warn("SENSOR_DISABLED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "SENSOR_DISABLED",
                        70,
                        true
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.SENSOR_DISABLED,
                    70,
                    true
            );
        }

        if (bluetoothJammed) {

            log.warn("BLUETOOTH_JAMMED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "BLUETOOTH_JAMMED",
                        60,
                        false
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.BLUETOOTH_JAMMED,
                    60,
                    false
            );
        }

        if (gpsJammed) {

            log.warn("GPS_JAMMED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "GPS_JAMMED",
                        60,
                        false
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.GPS_JAMMED,
                    60,
                    false
            );
        }

        if (powerDisconnected) {

            log.warn("POWER_DISCONNECTED");

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "POWER_DISCONNECTED",
                        40,
                        false
                );
            }

            return new TamperAnalysisResult(
                    TamperEventType.POWER_DISCONNECTED,
                    40,
                    false
            );
        }

        return new TamperAnalysisResult(
                TamperEventType.NONE,
                0,
                false
        );
    }
    private ActiveSafetySession getSession(
            String deviceId
    ) {

        return activeSafetySessionRepository
                .findByDeviceId(deviceId)
                .orElse(null);
    }
}
