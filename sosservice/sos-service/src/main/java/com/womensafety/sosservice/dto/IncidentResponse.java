package com.womensafety.sosservice.dto;

import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.domain.enums.IncidentTriggerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record IncidentResponse(

        UUID incidentId,

        UUID userId,

        String trackingId,

        IncidentTriggerType triggerType,

        IncidentStatus status,

        Integer riskScore,

        BigDecimal latitude,

        BigDecimal longitude,

        String incidentSource,

        LocalDateTime createdAt,

        LocalDateTime warningAt,

        LocalDateTime dangerAt,

        LocalDateTime trackingStartedAt,

        LocalDateTime resolvedAt,

        LocalDateTime closedAt

) {
}