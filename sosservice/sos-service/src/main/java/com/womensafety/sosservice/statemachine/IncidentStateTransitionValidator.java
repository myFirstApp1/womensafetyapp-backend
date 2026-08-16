package com.womensafety.sosservice.statemachine;

import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.exception.InvalidIncidentStateTransitionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class IncidentStateTransitionValidator {

    private static final Map<IncidentStatus, Set<IncidentStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(IncidentStatus.class);

    static {

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.CREATED,
                EnumSet.of(
                        IncidentStatus.WARNING,
                        IncidentStatus.TRACKING,
                        IncidentStatus.CLOSED
                )
        );

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.WARNING,
                EnumSet.of(
                        IncidentStatus.IN_DANGER,
                        IncidentStatus.RESOLVED
                )
        );

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.IN_DANGER,
                EnumSet.of(
                        IncidentStatus.TRACKING,
                        IncidentStatus.RESOLVED
                )
        );

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.TRACKING,
                EnumSet.of(
                        IncidentStatus.RESOLVED
                )
        );

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.RESOLVED,
                EnumSet.of(
                        IncidentStatus.CLOSED
                )
        );

        ALLOWED_TRANSITIONS.put(
                IncidentStatus.CLOSED,
                EnumSet.noneOf(IncidentStatus.class)
        );
    }

    public void validate(
            IncidentStatus from,
            IncidentStatus to
    ) {

        if (from == to) {
            return;
        }

        Set<IncidentStatus> allowed =
                ALLOWED_TRANSITIONS.get(from);

        if (allowed == null || !allowed.contains(to)) {

            log.error(
                    "Invalid Incident transition {} -> {}",
                    from,
                    to
            );

            throw new InvalidIncidentStateTransitionException(from, to);
        }

    }

}