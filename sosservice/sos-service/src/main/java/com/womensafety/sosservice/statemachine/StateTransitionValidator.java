package com.womensafety.sosservice.statemachine;

import com.womensafety.sosservice.domain.enums.SessionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class StateTransitionValidator {

    private final Map<SessionStatus, Set<SessionStatus>> allowedTransitions =
            new EnumMap<>(SessionStatus.class);

    public StateTransitionValidator() {

        allowedTransitions.put(
                SessionStatus.ACTIVE,
                EnumSet.of(
                        SessionStatus.SOFT_MONITORING,
                        SessionStatus.WARNING,
                        SessionStatus.PAUSED_MANUAL,
                        SessionStatus.PAUSED_OFF_BODY
                )
        );

        allowedTransitions.put(
                SessionStatus.WARNING,
                EnumSet.of(
                        SessionStatus.ACTIVE,
                        SessionStatus.IN_DANGER
                )
        );

        allowedTransitions.put(
                SessionStatus.IN_DANGER,
                EnumSet.of(
                        SessionStatus.RECOVERY_PENDING
                )
        );

        allowedTransitions.put(
                SessionStatus.RECOVERY_PENDING,
                EnumSet.of(
                        SessionStatus.SOFT_MONITORING,
                        SessionStatus.IN_DANGER
                )
        );

        allowedTransitions.put(
                SessionStatus.SOFT_MONITORING,
                EnumSet.of(
                        SessionStatus.ACTIVE,
                        SessionStatus.WARNING,
                        SessionStatus.IN_DANGER
                )
        );

        allowedTransitions.put(
                SessionStatus.PAUSED_MANUAL,
                EnumSet.of(
                        SessionStatus.ACTIVE
                )
        );

        allowedTransitions.put(
                SessionStatus.PAUSED_OFF_BODY,
                EnumSet.of(
                        SessionStatus.ACTIVE,
                        SessionStatus.IN_DANGER
                )
        );
    }

    public void validateTransition(
            SessionStatus currentState,
            SessionStatus nextState
    ) {

        Set<SessionStatus> validStates =
                allowedTransitions.get(currentState);

        if (validStates == null || !validStates.contains(nextState)) {

            log.error(
                    "Invalid state transition attempted: {} -> {}",
                    currentState,
                    nextState
            );

            throw new IllegalArgumentException(
                    String.format(
                            "Invalid transition from %s to %s",
                            currentState,
                            nextState
                    )
            );
        }

        log.info(
                "Valid transition: {} -> {}",
                currentState,
                nextState
        );
    }
}