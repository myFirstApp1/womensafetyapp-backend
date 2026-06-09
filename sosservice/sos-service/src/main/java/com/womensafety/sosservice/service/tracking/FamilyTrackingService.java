package com.womensafety.sosservice.service.tracking;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.dto.FamilyTrackingResponse;
import com.womensafety.sosservice.dto.TimelineItem;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import com.womensafety.sosservice.service.core.TimelineMapper;
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
    private final TimelineMapper timelineMapper;

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
                        .map(timelineMapper::toDto)
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
