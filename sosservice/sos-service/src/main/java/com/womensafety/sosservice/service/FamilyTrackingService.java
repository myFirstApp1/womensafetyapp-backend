package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.dto.FamilyTrackingResponse;
import com.womensafety.sosservice.dto.TimelineItem;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyTrackingService {

    private final ActiveSafetySessionRepository
            sessionRepository;

    private final EmergencyTimelineRepository
            timelineRepository;

    public FamilyTrackingResponse getTracking(
            String trackingId
    ) {

        ActiveSafetySession session =
                sessionRepository
                        .findByTrackingId(
                                trackingId
                        )
                        .orElseThrow();

        List<TimelineItem> timeline =
                timelineRepository
                        .findByTrackingIdOrderByCreatedAtAsc(
                                trackingId
                        )
                        .stream()
                        .map(event ->
                                TimelineItem.builder()
                                        .eventType(
                                                event.getEventType()
                                        )
                                        .eventData(
                                                event.getEventData()
                                        )
                                        .createdAt(
                                                event.getCreatedAt()
                                        )
                                        .build()
                        )
                        .toList();

        return FamilyTrackingResponse.builder()
                .trackingId(
                        trackingId
                )
                .status(
                        session.getStatus().name()
                )
                .riskScore(
                        session.getRiskScore()
                )
                .latitude(
                        session.getLastLatitude() == null
                                ? null
                                : session.getLastLatitude().doubleValue()
                )
                .longitude(
                        session.getLastLongitude() == null
                                ? null
                                : session.getLastLongitude().doubleValue()
                )
                .lastPingTime(
                        session.getLastPingTime()
                )
                .timeline(
                        timeline
                )
                .build();
    }
}