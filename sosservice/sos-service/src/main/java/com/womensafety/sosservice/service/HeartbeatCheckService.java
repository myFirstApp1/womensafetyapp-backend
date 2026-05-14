package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatCheckService {

    private final ActiveSafetySessionRepository activeSafetySessionRepository;
    private final SosOutboxRepository sosOutboxRepository;

    // =========================================
    // CONFIG
    // =========================================

    @Value("${safety.hr.max}")
    private int heartRateMax;

    @Value("${safety.hr.min}")
    private int heartRateMin;

    @Value("${safety.movement.danger}")
    private int dangerMovementThreshold;

    private static final int HEARTBEAT_TIMEOUT_MINUTES = 3;
    private static final int WARNING_TIMEOUT_SECONDS = 60;
    private static final int BLUETOOTH_TIMEOUT_MINUTES = 2;
    private static final int PAUSE_ESCALATION_MINUTES = 10;

    // =========================================
    // MAIN SCHEDULER
    // =========================================

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkHeartbeats() {

        log.info("HEARTBEAT_CHECK | Running scheduled job");

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime heartbeatThreshold =
                now.minusMinutes(HEARTBEAT_TIMEOUT_MINUTES);

        LocalDateTime bluetoothThreshold =
                now.minusMinutes(BLUETOOTH_TIMEOUT_MINUTES);

        LocalDateTime pauseThreshold =
                now.minusMinutes(PAUSE_ESCALATION_MINUTES);

        LocalDateTime warningThreshold =
                now.minusSeconds(WARNING_TIMEOUT_SECONDS);

        List<ActiveSafetySession> sessions =
                activeSafetySessionRepository.findByStatusIn(
                        List.of(
                                SessionStatus.ACTIVE,
                                SessionStatus.WARNING,
                                SessionStatus.PAUSED_MANUAL,
                                SessionStatus.PAUSED_OFF_BODY
                        )
                );

        for (ActiveSafetySession session : sessions) {

            try {

                // =========================================
                // 1. AUTO RESUME
                // =========================================

                if (session.getAutoResumeAt() != null &&
                        session.getAutoResumeAt().isBefore(now)) {

                    log.info("AUTO_RESUME | userId={}",
                            session.getUserId());

                    session.setStatus(SessionStatus.ACTIVE);
                    session.setPauseType(null);
                    session.setAutoResumeAt(null);

                    activeSafetySessionRepository.save(session);

                    continue;
                }

                // =========================================
                // 2. HEARTBEAT MISSING
                // ACTIVE -> WARNING
                // =========================================

                if (session.getStatus() == SessionStatus.ACTIVE &&
                        session.getLastPingTime() != null &&
                        session.getLastPingTime().isBefore(heartbeatThreshold)) {

                    log.warn("⚠️ WARNING_TRIGGER | heartbeat missing | userId={}",
                            session.getUserId());

                    moveToWarning(session, now);

                    continue;
                }

                // =========================================
                // 3. BLUETOOTH DISCONNECT
                // =========================================

                if (session.getStatus() == SessionStatus.ACTIVE &&
                        session.getLastBluetoothSeenAt() != null &&
                        session.getLastBluetoothSeenAt().isBefore(bluetoothThreshold)) {

                    log.warn("📡 DEVICE_DISCONNECTED | userId={}",
                            session.getUserId());

                    moveToWarning(session, now);

                    continue;
                }

                // =========================================
                // 4. USER CONFIRMED SAFE
                // WARNING -> ACTIVE
                // =========================================

                if (session.getStatus() == SessionStatus.WARNING &&
                        session.getConfirmationStatus() == ConfirmationStatus.SAFE_CONFIRMED) {

                    log.info("✅ USER_CONFIRMED_SAFE | userId={}",
                            session.getUserId());

                    session.setStatus(SessionStatus.ACTIVE);

                    session.setConfirmationStatus(
                            ConfirmationStatus.NONE
                    );

                    session.setWarningTriggeredAt(null);

                    activeSafetySessionRepository.save(session);

                    continue;
                }

                // =========================================
                // 5. WARNING -> SOS
                // =========================================

                if (session.getStatus() == SessionStatus.WARNING &&
                        session.getConfirmationStatus() == ConfirmationStatus.PENDING &&
                        session.getWarningTriggeredAt() != null &&
                        session.getWarningTriggeredAt().isBefore(warningThreshold) &&
                        !Boolean.TRUE.equals(session.getEmergencyTriggered())) {

                    log.error("🚨 NO_RESPONSE -> SOS | userId={}",
                            session.getUserId());

                    session.setConfirmationStatus(
                            ConfirmationStatus.NO_RESPONSE
                    );

                    triggerSosViaOutbox(
                            session,
                            "NO_RESPONSE"
                    );

                    continue;
                }

                // =========================================
                // 6. PAUSED -> WARNING
                // =========================================

                if ((session.getStatus() == SessionStatus.PAUSED_MANUAL ||
                        session.getStatus() == SessionStatus.PAUSED_OFF_BODY) &&
                        session.getLastPingTime() != null &&
                        session.getLastPingTime().isBefore(pauseThreshold)) {

                    log.warn("⚠️ PAUSED_ESCALATION | userId={}",
                            session.getUserId());

                    moveToWarning(session, now);
                }

            } catch (Exception e) {

                log.error("HEARTBEAT_CHECK_ERROR | userId={}",
                        session.getUserId(),
                        e);
            }
        }
    }

    // =========================================
    // MOVE TO WARNING
    // =========================================

    private void moveToWarning(ActiveSafetySession session,
                               LocalDateTime now) {

        session.setStatus(SessionStatus.WARNING);

        session.setConfirmationStatus(
                ConfirmationStatus.PENDING
        );

        session.setWarningTriggeredAt(now);

        activeSafetySessionRepository.save(session);

        log.warn("📲 PUSH_NOTIFICATION | Are you safe? | userId={}",
                session.getUserId());
    }

    // =========================================
    // START PROTECTION
    // =========================================

    @Transactional
    public void startProtection(UUID userId) {

        log.info("PROTECTION_START | userId={}",
                userId);

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElse(new ActiveSafetySession());

        session.setUserId(userId);

        session.setStatus(SessionStatus.ACTIVE);

        session.setPauseType(null);

        session.setConfirmationStatus(
                ConfirmationStatus.NONE
        );

        session.setAutoResumeAt(null);

        session.setIsDeviceWorn(true);

        session.setEmergencyTriggered(false);

        session.setEmergencyContactNotified(false);

        session.setSessionStartTime(LocalDateTime.now());

        session.setLastPingTime(LocalDateTime.now());

        // 🔥 IMPORTANT
        session.setLastBluetoothSeenAt(LocalDateTime.now());

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // HEARTBEAT PING
    // =========================================

    @Transactional
    public void ping(UUID userId) {

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    session.setLastPingTime(LocalDateTime.now());

                    // 🔥 Heartbeat implies bluetooth alive
                    session.setLastBluetoothSeenAt(
                            LocalDateTime.now()
                    );

                    // RECOVERY
                    if (session.getStatus() == SessionStatus.WARNING ||
                            session.getStatus() == SessionStatus.PAUSED_MANUAL ||
                            session.getStatus() == SessionStatus.PAUSED_OFF_BODY) {

                        log.info("RECOVERY -> ACTIVE | userId={}",
                                userId);

                        session.setStatus(SessionStatus.ACTIVE);

                        session.setPauseType(null);

                        session.setAutoResumeAt(null);

                        session.setConfirmationStatus(
                                ConfirmationStatus.NONE
                        );

                        session.setWarningTriggeredAt(null);

                        session.setIsDeviceWorn(true);
                    }

                    activeSafetySessionRepository.save(session);
                });
    }

    // =========================================
    // STOP PROTECTION
    // =========================================

    @Transactional
    public void stopProtection(UUID userId) {

        log.info("PROTECTION_STOP | userId={}",
                userId);

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    session.setStatus(SessionStatus.ENDED);

                    activeSafetySessionRepository.save(session);
                });
    }

    // =========================================
    // PAUSE PROTECTION
    // =========================================

    @Transactional
    public void pauseProtection(UUID userId,
                                int minutes) {

        log.info("PROTECTION_PAUSE | userId={} | duration={} mins",
                userId,
                minutes);

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn("SESSION_NOT_FOUND -> Creating | userId={}",
                                    userId);

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);

                            newSession.setStatus(SessionStatus.ACTIVE);

                            newSession.setSessionStartTime(LocalDateTime.now());

                            return activeSafetySessionRepository.save(newSession);
                        });

        session.setStatus(SessionStatus.PAUSED_MANUAL);

        session.setPauseType(PauseType.MANUAL);

        // manual pause ≠ device removed
        session.setIsDeviceWorn(true);

        session.setAutoResumeAt(
                LocalDateTime.now().plusMinutes(minutes)
        );

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // RESUME PROTECTION
    // =========================================

    @Transactional
    public void resumeProtection(UUID userId) {

        log.info("PROTECTION_RESUME | userId={}",
                userId);

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        session.setStatus(SessionStatus.ACTIVE);

        session.setPauseType(null);

        session.setAutoResumeAt(null);

        session.setConfirmationStatus(
                ConfirmationStatus.NONE
        );

        session.setWarningTriggeredAt(null);

        session.setIsDeviceWorn(true);

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // USER SAFE CONFIRMATION
    // =========================================

    @Transactional
    public void confirmUserSafe(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        log.info("USER_CONFIRMED_SAFE | userId={}",
                userId);

        session.setStatus(SessionStatus.ACTIVE);

        session.setConfirmationStatus(
                ConfirmationStatus.SAFE_CONFIRMED
        );

        session.setWarningTriggeredAt(null);

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // BLUETOOTH PING
    // =========================================

    @Transactional
    public void updateBluetoothPing(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn("SESSION_NOT_FOUND -> Creating | userId={}",
                                    userId);

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);

                            newSession.setStatus(SessionStatus.ACTIVE);

                            newSession.setSessionStartTime(LocalDateTime.now());

                            return activeSafetySessionRepository.save(newSession);
                        });

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        // BLUETOOTH RECOVERY
        if (session.getStatus() == SessionStatus.WARNING) {

            log.info("📡 BLUETOOTH_RECOVERY | userId={}",
                    userId);

            session.setStatus(SessionStatus.ACTIVE);

            session.setConfirmationStatus(
                    ConfirmationStatus.NONE
            );

            session.setWarningTriggeredAt(null);
        }

        // DEVICE WORN AGAIN
        if (session.getStatus() == SessionStatus.PAUSED_OFF_BODY) {

            log.info("⌚ DEVICE_WORN_AGAIN | userId={}",
                    userId);

            session.setStatus(SessionStatus.ACTIVE);

            session.setPauseType(null);

            session.setIsDeviceWorn(true);

            session.setConfirmationStatus(
                    ConfirmationStatus.NONE
            );

            session.setWarningTriggeredAt(null);
        }

        activeSafetySessionRepository.save(session);

        log.info("📡 BLUETOOTH_PING | userId={}",
                userId);
    }

    // =========================================
    // DEVICE VITALS
    // =========================================

    @Transactional
    public void updateVitals(UUID userId,
                             int heartRate,
                             int movementScore) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn("SESSION_NOT_FOUND -> Creating | userId={}",
                                    userId);

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);

                            newSession.setStatus(SessionStatus.ACTIVE);

                            newSession.setSessionStartTime(LocalDateTime.now());

                            newSession.setEmergencyTriggered(false);

                            newSession.setEmergencyContactNotified(false);

                            return activeSafetySessionRepository.save(newSession);
                        });

        session.setLastPingTime(LocalDateTime.now());

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        session.setLastHeartRate(heartRate);

        session.setMovementScore(movementScore);

        log.info("💓 VITALS | userId={} | HR={} | movement={}",
                userId,
                heartRate,
                movementScore);

        // 🚨 EMERGENCY OVERRIDE
        boolean abnormalHeartRate =
                heartRate > heartRateMax ||
                        heartRate < heartRateMin;

        boolean violentMovement =
                movementScore > dangerMovementThreshold;

        if (abnormalHeartRate && violentMovement) {

            forceEmergency(
                    session,
                    "HEART_RATE_AND_MOVEMENT"
            );

            return;
        }

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // FORCE EMERGENCY
    // =========================================

    private void forceEmergency(ActiveSafetySession session,
                                String reason) {

        if (Boolean.TRUE.equals(session.getEmergencyTriggered())) {
            return;
        }

        log.error("🚨 FORCE_SOS | userId={} | reason={}",
                session.getUserId(),
                reason);

        triggerSosViaOutbox(session, reason);
    }

    // =========================================
    // OUTBOX SOS
    // =========================================

    private void triggerSosViaOutbox(ActiveSafetySession session,
                                     String triggerReason) {

        String location = "UNKNOWN";

        if (session.getLastLatitude() != null &&
                session.getLastLongitude() != null) {

            location =
                    session.getLastLatitude().toPlainString()
                            + "," +
                            session.getLastLongitude().toPlainString();
        }

        log.error("🚨 SOS_TRIGGERED | userId={} | reason={} | location={}",
                session.getUserId(),
                triggerReason,
                location);

        // Generate tracking only once
        if (session.getTrackingId() == null) {

            session.setTrackingId(
                    UUID.randomUUID().toString()
            );
        }

        SosOutbox event = new SosOutbox();

        event.setUserId(session.getUserId());

        event.setLocation(location);

        event.setStatus("PENDING");

        // optional future field
        // event.setTriggerReason(triggerReason);

        sosOutboxRepository.save(event);

        session.setStatus(SessionStatus.IN_DANGER);

        session.setEmergencyTriggered(true);

        session.setEmergencyContactNotified(false);

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // OFF BODY
    // =========================================

    @Transactional
    public void markDeviceOffBody(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        log.warn("📴 DEVICE_OFF_BODY | userId={}",
                userId);

        session.setStatus(SessionStatus.PAUSED_OFF_BODY);

        session.setPauseType(PauseType.OFF_BODY);

        session.setIsDeviceWorn(false);

        activeSafetySessionRepository.save(session);
    }

    // =========================================
    // HELPERS
    // =========================================

    public List<ActiveSafetySession> getProtectedUsers() {

        return activeSafetySessionRepository
                .findAllByStatus(SessionStatus.ACTIVE);
    }

    public boolean isUserProtected(UUID userId) {

        return activeSafetySessionRepository.findById(userId)
                .map(session ->
                        session.getStatus() == SessionStatus.ACTIVE)
                .orElse(false);
    }

    public List<ActiveSafetySession> getStaleUsers() {

        LocalDateTime threshold =
                LocalDateTime.now().minusMinutes(3);

        return activeSafetySessionRepository
                .findByStatusAndLastPingTimeBefore(
                        SessionStatus.ACTIVE,
                        threshold
                );
    }
}