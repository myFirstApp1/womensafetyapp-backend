package com.womensafety.sosservice.dto;

import com.womensafety.sosservice.domain.enums.IncidentTriggerType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record IncidentRequest(

        @NotNull
        UUID userId,

        String trackingId,

        @NotNull
        IncidentTriggerType triggerType,

        Integer riskScore,

        BigDecimal latitude,

        BigDecimal longitude,

        String incidentSource

) {
}