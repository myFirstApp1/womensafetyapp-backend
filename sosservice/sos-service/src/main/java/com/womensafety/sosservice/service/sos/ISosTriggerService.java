package com.womensafety.sosservice.service.sos;

import com.womensafety.sosservice.domain.ActiveSafetySession;

/**
 * Service responsible for triggering SOS events and managing outbox notifications.
 * Handles SOS trigger logic, state transitions, and emergency timeline recording.
 */
public interface ISosTriggerService {

    /**
     * Trigger SOS via outbox for a session with a given reason.
     * Creates outbox event, records timeline, and transitions session state to IN_DANGER.
     *
     * @param session the active safety session
     * @param triggerReason reason for SOS trigger (e.g., "MANUAL_SOS", "HEART_RATE_AND_MOVEMENT")
     */
    void triggerSosViaOutbox(ActiveSafetySession session, String triggerReason);
}
