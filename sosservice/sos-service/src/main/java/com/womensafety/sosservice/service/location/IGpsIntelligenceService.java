package com.womensafety.sosservice.service.location;

import com.womensafety.sosservice.domain.GpsAnalysisResult;

/**
 * Service for GPS-based location intelligence and movement analysis.
 * Analyzes location history to detect stationery, movement patterns, and risky behaviors.
 */
public interface IGpsIntelligenceService {

    /**
     * Analyze GPS location history for a device.
     * Returns risk assessment based on movement patterns and location changes.
     *
     * @param deviceId the device ID
     * @return GPS analysis result with status, risk score, and reason
     */
    GpsAnalysisResult analyze(String deviceId);
}
