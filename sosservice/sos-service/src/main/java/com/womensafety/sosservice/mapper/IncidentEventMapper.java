package com.womensafety.sosservice.mapper;

import com.womensafety.sosservice.domain.IncidentEvent;
import com.womensafety.sosservice.dto.IncidentEventResponse;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventMapper {

    public IncidentEventResponse toResponse(
            IncidentEvent event
    ) {

        return new IncidentEventResponse(

                event.getEventId(),
                event.getEventType(),
                event.getTitle(),
                event.getDescription(),
                event.getCreatedAt()

        );
    }

}
