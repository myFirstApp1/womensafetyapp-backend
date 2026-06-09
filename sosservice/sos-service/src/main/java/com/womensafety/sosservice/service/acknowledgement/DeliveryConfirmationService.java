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
public class DeliveryConfirmationService {

    private final IncidentDispatchRepository
            repository;
    private final EmergencyTimelineService
            timelineService;

    private final SessionManager sessionManager;


    public void markDelivered(
            String trackingId
    ) {

        repository
                .findByTrackingId(
                        trackingId
                )
                .forEach(dispatch -> {

                    dispatch.setDeliveryStatus(
                            IncidentDeliveryStatus.DELIVERED
                    );

                    dispatch.setDeliveredAt(
                            LocalDateTime.now()
                    );

                    sessionManager.recordEventByTrackingId(
                            trackingId,
                            "SOS_DELIVERED",
                            "Emergency packet delivered"
                    );

                    repository.save(
                            dispatch
                    );
                });

    }
}
