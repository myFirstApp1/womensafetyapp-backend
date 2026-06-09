package com.womensafety.sosservice.service.prealert;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.PreAlertStatus;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PreAlertService {

    private final ActiveSafetySessionRepository repository;

    private final EmergencyTimelineService timelineService;

    private final SessionManager sessionManager;

    public void startPreAlert(
            ActiveSafetySession session
    ) {

        session.setPreAlertStatus(
                PreAlertStatus.ACTIVE
        );

        session.setPreAlertStartedAt(
                LocalDateTime.now()
        );

        sessionManager.save(session);

        sessionManager.recordEvent(
                session,
                "PRE_ALERT_STARTED",
                "User feels unsafe"
        );
    }

    public void cancelPreAlert(
            ActiveSafetySession session
    ) {

        session.setPreAlertStatus(
                PreAlertStatus.CANCELLED
        );

        sessionManager.save(session);

        sessionManager.recordEvent(
                session,
                "PRE_ALERT_CANCELLED",
                "User cancelled pre-alert"
        );
    }
}
