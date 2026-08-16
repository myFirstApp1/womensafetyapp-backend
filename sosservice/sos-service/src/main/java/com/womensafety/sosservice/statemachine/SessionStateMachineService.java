package com.womensafety.sosservice.statemachine;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.SessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionStateMachineService {

    private final StateTransitionValidator validator;

    public void transitionState(
            ActiveSafetySession session,
            SessionStatus newState,
            String reason,
            String source
    ) {

        SessionStatus oldState =
                session.getStatus();

        // =========================================
        // INITIAL STATE BOOTSTRAP
        // =========================================

        if (oldState == null &&
                newState == SessionStatus.ACTIVE) {

            session.setStatus(newState);

            log.info("""
                    INITIAL_STATE
                    userId={}
                    newState={}
                    reason={}
                    source={}
                    """,
                    session.getUserId(),
                    newState,
                    reason,
                    source
            );

            return;
        }

        log.info("""

==============================
STATE MACHINE REQUEST
Current = {}
Next    = {}
Reason  = {}
Source  = {}
==============================
""",
                oldState,
                newState,
                reason,
                source
        );

        // =========================================
        // VALIDATE TRANSITION
        // =========================================

        validator.validateTransition(
                oldState,
                newState
        );

        // =========================================
        // APPLY TRANSITION
        // =========================================

        session.setStatus(newState);

        log.info("""
                STATE_TRANSITION
                userId={}
                trackingId={}
                oldState={}
                newState={}
                reason={}
                source={}
                """,
                session.getUserId(),
                session.getTrackingId(),
                oldState,
                newState,
                reason,
                source
        );
    }
}