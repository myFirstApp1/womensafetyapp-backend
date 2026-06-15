package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.OffBodyAnalysisResult;
import com.womensafety.sosservice.domain.enums.OffBodyEventType;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.incident.IncidentResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OffBodyIntelligenceService {
    private final IncidentResponseService
            incidentResponseService;

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;
        private final SessionManager sessionManager;
    public OffBodyAnalysisResult analyze(
            String deviceId,
            boolean deviceWorn,
            int heartRate,
            int movementScore,
            boolean bluetoothConnected
    ) {

        // ==========================================
        // DEVICE STILL WORN
        // ==========================================

        if (deviceWorn) {

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null &&
                    session.getLastOffBodyEvent() != null) {

                session.setLastOffBodyEvent(null);

                sessionManager.save(session);
            }

            return new OffBodyAnalysisResult(
                    OffBodyEventType.UNKNOWN,
                    0,
                    false
            );
        }

        // ==========================================
        // STRAP CUT / EXTREME ATTACK
        // ==========================================

        if (movementScore >= 90
                && heartRate >= 140) {

            log.error(
                    "EXTREME_VIOLENT_REMOVAL | HR={} | movement={}",
                    heartRate,
                    movementScore
            );

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "EXTREME_VIOLENT_REMOVAL",
                        90,
                        true
                );
            }

            return new OffBodyAnalysisResult(
                    OffBodyEventType.EXTREME_VIOLENT_REMOVAL,
                    90,
                    true
            );
        }

        // ==========================================
        // VIOLENT REMOVAL
        // ==========================================

        if (movementScore >= 70
                && heartRate >= 120) {

            log.warn(
                    "VIOLENT_REMOVAL_DETECTED | HR={} | movement={}",
                    heartRate,
                    movementScore
            );
            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "VIOLENT_REMOVAL",
                        80,
                        true
                );
            }

            return new OffBodyAnalysisResult(
                    OffBodyEventType.VIOLENT_REMOVAL,
                    80,
                    true
            );
        }

        // ==========================================
        // DEVICE THROWN AWAY
        // ==========================================

        if (movementScore >= 80) {

            log.warn(
                    "DEVICE_THROWN_DETECTED | movement={}",
                    movementScore
            );

            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {

                incidentResponseService.processIncident(
                        session,
                        "DEVICE_THROWN",
                        70,
                        true
                );
            }

            return new OffBodyAnalysisResult(
                    OffBodyEventType.DEVICE_THROWN,
                    70,
                    true
            );
        }

        // ==========================================
        // CHARGING MODE
        // ==========================================

        if (!bluetoothConnected
                && movementScore < 10
                && heartRate < 100) {

            log.info(
                    "CHARGING_MODE_ASSUMED"
            );
            return new OffBodyAnalysisResult(
                    OffBodyEventType.CHARGING_MODE,
                    0,
                    false
            );
        }

        // ==========================================
        // SLOW REMOVAL
        // ==========================================

        if (movementScore < 30
                && heartRate < 110) {

            log.info(
                    "SLOW_REMOVAL_DETECTED"

            );
            ActiveSafetySession session =
                    getSession(deviceId);

            if (session != null) {
                if(session.getLastOffBodyEvent()
                        != OffBodyEventType.SLOW_REMOVAL)
                {

                incidentResponseService.processIncident(
                        session, 
                        "SLOW_REMOVAL",
                        20,
                        false
                );
                    session.setLastOffBodyEvent(
                            OffBodyEventType.SLOW_REMOVAL
                    );
                    sessionManager.save(
                            session
                    );

                }
            }
            return new OffBodyAnalysisResult(
                    OffBodyEventType.SLOW_REMOVAL,
                    20,
                    false
            );
        }

        // ==========================================
        // UNKNOWN
        // ==========================================

        log.warn(
                "UNKNOWN_OFF_BODY_EVENT | HR={} | movement={}",
                heartRate,
                movementScore
        );

        return new OffBodyAnalysisResult(
                OffBodyEventType.UNKNOWN,
                10,
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