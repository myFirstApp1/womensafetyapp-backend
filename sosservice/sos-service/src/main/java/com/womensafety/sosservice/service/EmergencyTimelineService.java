package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.EmergencyTimeline;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyTimelineService {

    private final EmergencyTimelineRepository
            repository;

    public void recordEvent(
            ActiveSafetySession session,
            String eventType,
            String eventData
    ) {

        EmergencyTimeline event =
                EmergencyTimeline.builder()
                        .trackingId(
                                session.getTrackingId()
                        )
                        .userId(
                                session.getUserId()
                        )
                        .eventType(
                                eventType
                        )
                        .eventData(
                                eventData
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        repository.save(event);

        log.info(
                "TIMELINE_EVENT | trackingId={} | type={}",
                session.getTrackingId(),
                eventType
        );
    }
}