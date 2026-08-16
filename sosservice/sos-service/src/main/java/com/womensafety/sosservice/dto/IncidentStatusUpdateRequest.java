package com.womensafety.sosservice.dto;

import com.womensafety.sosservice.domain.enums.IncidentStatus;
import jakarta.validation.constraints.NotNull;

public record IncidentStatusUpdateRequest(

        @NotNull
        IncidentStatus status

) {
}
