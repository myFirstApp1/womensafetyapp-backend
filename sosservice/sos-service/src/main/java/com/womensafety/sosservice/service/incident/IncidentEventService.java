package com.womensafety.sosservice.service.incident;

import com.womensafety.sosservice.domain.enums.IncidentEventType;

import java.util.UUID;

public interface IncidentEventService {

    void recordEvent(
            UUID incidentId,
            UUID userId,
            String trackingId,
            IncidentEventType type,
            String title,
            String description
    );

}
