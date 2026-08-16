package com.womensafety.sosservice.statemachine;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidentStateMachineService {

    private final IncidentStateTransitionValidator validator;

    public void transition(
            Incident incident,
            IncidentStatus newStatus
    ) {

        validator.validate(
                incident.getStatus(),
                newStatus
        );

        incident.setStatus(newStatus);

    }

}