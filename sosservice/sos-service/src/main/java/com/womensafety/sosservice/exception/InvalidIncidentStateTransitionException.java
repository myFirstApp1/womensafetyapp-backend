package com.womensafety.sosservice.exception;

import com.womensafety.sosservice.domain.enums.IncidentStatus;

public class InvalidIncidentStateTransitionException extends RuntimeException {

    public InvalidIncidentStateTransitionException(
            IncidentStatus from,
            IncidentStatus to
    ) {
        super("Invalid incident transition: " + from + " -> " + to);
    }
}
