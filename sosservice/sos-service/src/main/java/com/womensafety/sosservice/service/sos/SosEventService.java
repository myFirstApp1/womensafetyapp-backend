package com.womensafety.sosservice.service.sos;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.PreAlertStatus;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.prealert.PreAlertService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import com.womensafety.sosservice.service.core.SessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SosEventService {

    private final ActiveSafetySessionRepository repository;

    private final SosTriggerService sosTriggerService;

    private final EmergencyTimelineService timelineService;

    private final PreAlertService preAlertService;
    private final SessionManager sessionManager;

    public void processEvent(
            UUID userId,
            String event,
            Double lat,
            Double lng
    ) {

        ActiveSafetySession session =
                repository.findById(userId)
                        .orElseThrow();

        switch (event) {

            case "SOS_BUTTON_PRESSED":
                handleSosPressed(
                        session,
                        lat,
                        lng
                );
                break;

            case "MARK_SAFE":
                handleMarkSafe(session);
                break;

            case "PRE_ALERT":
                handlePreAlert(session);
                break;

            case "CANCEL_PRE_ALERT":
                handleCancelPreAlert(session);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported event: " + event
                );
        }
    }

    private void handleSosPressed(
            ActiveSafetySession session,
            Double lat,
            Double lng
    ) {

        sessionManager.recordEvent(
                session,
                "SOS_BUTTON_PRESSED",
                "User manually pressed SOS"
        );
        if (lat != null) {

            session.setLastLatitude(
                    BigDecimal.valueOf(lat)
            );
        }

        if (lng != null) {

            session.setLastLongitude(
                    BigDecimal.valueOf(lng)
            );
        }

        sessionManager.save(session);
        sosTriggerService.triggerSosViaOutbox(
                session,
                "MANUAL_SOS"
        );
    }

    private void handleMarkSafe(
            ActiveSafetySession session
    ) {

        session.setEmergencyTriggered(false);

        session.setPreAlertStatus(
                PreAlertStatus.NONE
        );

        session.setPreAlertStartedAt(null);

        repository.save(session);

        sessionManager.recordEvent(
                session,
                "MARK_SAFE",
                "User marked safe"
        );
    }

    private void handleCancelPreAlert(
            ActiveSafetySession session
    ) {
        preAlertService.cancelPreAlert(
                session
        );

    }

    private void handlePreAlert(
            ActiveSafetySession session
    ) {
        preAlertService.startPreAlert(
                session
        );
    }
}
