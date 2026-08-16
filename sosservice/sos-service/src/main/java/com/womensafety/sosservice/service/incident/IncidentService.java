package com.womensafety.sosservice.service.incident;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.domain.enums.IncidentTriggerType;
import com.womensafety.sosservice.dto.IncidentEventResponse;
import com.womensafety.sosservice.dto.IncidentResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(UUID userId,
                            String trackingId,
                            IncidentTriggerType triggerType,
                            Integer riskScore,
                            BigDecimal latitude,
                            BigDecimal longitude,
                            String incidentSource);

    IncidentResponse getIncident(UUID incidentId);

    Incident findById(UUID incidentId);


    List<IncidentResponse> getUserIncidents(UUID userId);

    IncidentResponse getActiveIncident(UUID userId);

    IncidentResponse updateStatus(
            UUID incidentId,
            IncidentStatus status
    );

    boolean existsActiveIncident(UUID userId);

    List<IncidentEventResponse> getTimeline(UUID incidentId);
}
