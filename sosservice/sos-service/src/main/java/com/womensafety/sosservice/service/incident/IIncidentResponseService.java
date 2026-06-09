package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;

/**
 * Service for processing and responding to detected incidents.
 * Records incident events, updates risk scores, and triggers SOS if necessary.
 */
public interface IIncidentResponseService {

    /**
     * Process a detected incident for a session.
     * Records timeline event, updates risk score, and optionally triggers SOS.
     *
     * @param session the active safety session
     * @param incidentType type of incident (e.g., "STATIONARY", "OFF_BODY", "VIOLENT_REMOVAL")
     * @param riskIncrease additional risk points to add to session risk score
     * @param triggerSos whether to trigger SOS for this incident
     */
    void processIncident(
            ActiveSafetySession session,
            String incidentType,
            Integer riskIncrease,
            boolean triggerSos
    );
}
