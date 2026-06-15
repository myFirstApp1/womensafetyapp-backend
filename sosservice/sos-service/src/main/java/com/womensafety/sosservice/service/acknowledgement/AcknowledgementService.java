package com.womensafety.sosservice.service.acknowledgement;

import com.womensafety.sosservice.domain.enums.IncidentDeliveryStatus;
import com.womensafety.sosservice.repository.IncidentDispatchRepository;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AcknowledgementService {

    private final IncidentDispatchRepository
            repository;
    private final EmergencyTimelineService timelineService;

    private final SessionManager sessionManager;

    public void markAcknowledged(
            String trackingId
    ) {

        repository.findByTrackingId(
                trackingId
        ).forEach(dispatch -> {

            dispatch.setDeliveryStatus(
                    IncidentDeliveryStatus.ACKNOWLEDGED
            );

            dispatch.setAcknowledgedAt(
                    LocalDateTime.now()
            );

            repository.save(
                    dispatch
            );
            sessionManager.recordEventByTrackingId(
                    trackingId,
                    "POLICE_ACK",
                    "Police acknowledged incident"
            );
        });
    }
}