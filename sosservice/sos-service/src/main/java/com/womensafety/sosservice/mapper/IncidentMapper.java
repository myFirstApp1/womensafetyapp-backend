package com.womensafety.sosservice.mapper;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.dto.IncidentResponse;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {

    public IncidentResponse toResponse(Incident incident) {

        if (incident == null) {
            return null;
        }

        return new IncidentResponse(

                incident.getIncidentId(),

                incident.getUserId(),

                incident.getTrackingId(),

                incident.getTriggerType(),

                incident.getStatus(),

                incident.getRiskScore(),

                incident.getLatitude(),

                incident.getLongitude(),

                incident.getIncidentSource(),

                incident.getCreatedAt(),

                incident.getWarningAt(),

                incident.getDangerAt(),

                incident.getTrackingStartedAt(),

                incident.getResolvedAt(),

                incident.getClosedAt()

        );
    }

}
