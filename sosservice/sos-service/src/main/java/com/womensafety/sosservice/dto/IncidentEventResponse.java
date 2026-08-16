package com.womensafety.sosservice.dto;

import com.womensafety.sosservice.domain.enums.IncidentEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentEventResponse(

        UUID eventId,

        IncidentEventType eventType,

        String title,

        String description,

        LocalDateTime createdAt

) {
}
