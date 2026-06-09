package com.womensafety.sosservice.service.core;

import com.womensafety.sosservice.dto.TimelineItem;
import com.womensafety.sosservice.domain.EmergencyTimeline;
import org.springframework.stereotype.Component;

@Component
public class TimelineMapper {

    public TimelineItem toDto(EmergencyTimeline event) {
        return TimelineItem.builder()
                .eventType(event.getEventType())
                .eventData(event.getEventData())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
