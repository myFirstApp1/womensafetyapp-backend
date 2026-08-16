package com.womensafety.sosservice.service.timeline;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.IncidentEvent;
import com.womensafety.sosservice.domain.enums.IncidentEventType;
import com.womensafety.sosservice.dto.IncidentEventResponse;
import com.womensafety.sosservice.mapper.IncidentEventMapper;
import com.womensafety.sosservice.repository.IncidentEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentTimelineService {

    private final IncidentEventRepository repository;
    private final IncidentEventMapper mapper;

    public void addEvent(
            Incident incident,
            IncidentEventType eventType,
            String title,
            String description
    ) {

        IncidentEvent event = IncidentEvent.builder()
                .eventId(UUID.randomUUID())
                .incidentId(incident.getIncidentId())
                .userId(incident.getUserId())
                .trackingId(incident.getTrackingId())
                .eventType(eventType)
                .title(title)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(event);

        log.info(
                "TIMELINE_EVENT | incident={} | event={}",
                incident.getIncidentId(),
                eventType
        );
    }

    public List<IncidentEventResponse> getTimeline(UUID incidentId) {

        return repository
                .findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    };

}