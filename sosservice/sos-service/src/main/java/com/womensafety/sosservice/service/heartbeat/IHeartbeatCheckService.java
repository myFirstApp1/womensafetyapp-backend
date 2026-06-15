package com.womensafety.sosservice.service.heartbeat;

import java.util.List;
import java.util.UUID;
import com.womensafety.sosservice.domain.ActiveSafetySession;

/**
 * Service for managing user protection sessions and heartbeat monitoring.
 * Handles protection lifecycle, heartbeat validation, and risk escalation.
 */
public interface IHeartbeatCheckService {

    /**
     * Scheduled job to check heartbeats and escalate sessions based on timeout rules.
     */
    void checkHeartbeats();

    /**
     * Start protection session for a user.
     * @param userId the user ID
     */
    void startProtection(UUID userId);

    /**
     * Record a heartbeat/ping from the device.
     * @param userId the user ID
     */
    void ping(UUID userId);

    /**
     * Stop protection for a user.
     * @param userId the user ID
     */
    void stopProtection(UUID userId);

    /**
     * Pause protection for a specified duration.
     * @param userId the user ID
     * @param minutes duration in minutes
     */
    void pauseProtection(UUID userId, int minutes);

    /**
     * Resume protection for a user.
     * @param userId the user ID
     */
    void resumeProtection(UUID userId);

    /**
     * Mark user as safe (from WARNING state).
     * @param userId the user ID
     */
    void confirmUserSafe(UUID userId);

    /**
     * Update Bluetooth heartbeat ping timestamp.
     * @param userId the user ID
     */
    void updateBluetoothPing(UUID userId);

    /**
     * Record vitals (heart rate, movement) from wearable device.
     * @param userId the user ID
     * @param heartRate heart rate in BPM
     * @param movementScore movement intensity score
     */
    void updateVitals(UUID userId, int heartRate, int movementScore);

    /**
     * Mark device as removed from body.
     * @param userId the user ID
     */
    void markDeviceOffBody(UUID userId);

    /**
     * Get list of users with active protection.
     * @return list of active sessions
     */
    List<ActiveSafetySession> getProtectedUsers();

    /**
     * Check if user is protected.
     * @param userId the user ID
     * @return true if user is in ACTIVE status
     */
    boolean isUserProtected(UUID userId);

    /**
     * Get list of users with stale heartbeats.
     * @return list of stale sessions
     */
    List<ActiveSafetySession> getStaleUsers();
}
