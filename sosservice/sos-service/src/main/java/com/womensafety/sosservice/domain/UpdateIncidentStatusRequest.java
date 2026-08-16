package com.womensafety.sosservice.domain;


import com.womensafety.sosservice.domain.enums.IncidentStatus;

public record UpdateIncidentStatusRequest(
        IncidentStatus status
) {
}
