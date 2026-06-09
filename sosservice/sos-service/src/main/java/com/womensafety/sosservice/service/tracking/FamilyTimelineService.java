package com.womensafety.sosservice.service.tracking;

import com.womensafety.sosservice.dto.TimelineEventResponse;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyTimelineService {

    private final EmergencyTimelineRepository repository;

    public List<TimelineEventResponse>
    getTimeline(
            String trackingId
    ) {

        return repository
                .findByTrackingIdOrderByCreatedAtAsc(
                        trackingId
                )
                .stream()
                .map(event ->
                        TimelineEventResponse.builder()
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
    }
}
