package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import com.womensafety.sosservice.statemachine.SessionStateMachineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    private final SessionStateMachineService stateMachineService;
    private final RiskScoreCalculatorService riskScoreCalculatorService;
    private final CommunicationFallbackService communicationFallbackService;
    private final EmergencyCommunicationService emergencyCommunicationService;
    private final OffBodyIntelligenceService offBodyIntelligenceService;
    private final EmergencyTimelineService emergencyTimelineService;
    private final SosTriggerService sosTriggerService;

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

                    stateMachineService.transitionState(
                            session,
                            SessionStatus.ACTIVE,
                            "AUTO_RESUME",
                            "SCHEDULER"
                    );

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

                    log.info(" USER_CONFIRMED_SAFE | userId={}",
                            session.getUserId());

                    stateMachineService.transitionState(
                            session,
                            SessionStatus.ACTIVE,
                            "SAFE_CONFIRMED",
                            "SCHEDULER"
                    );

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

                    sosTriggerService.triggerSosViaOutbox(
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

            } catch (ObjectOptimisticLockingFailureException e) {

                log.warn(
                        "OPTIMISTIC_LOCK_CONFLICT | userId={}",
                        session.getUserId()
                );

            } catch (Exception e) {

                log.error(
                        "HEARTBEAT_CHECK_ERROR | userId={}",
                        session.getUserId(),
                        e
                );
            }
        }
    }

    private void moveToWarning(ActiveSafetySession session,
                               LocalDateTime now) {

        stateMachineService.transitionState(
                session,
                SessionStatus.WARNING,
                "HEARTBEAT_TIMEOUT",
                "SCHEDULER"
        );

        session.setConfirmationStatus(
                ConfirmationStatus.PENDING
        );

        session.setWarningTriggeredAt(now);

        activeSafetySessionRepository.save(session);

        log.warn("📲 PUSH_NOTIFICATION | Are you safe? | userId={}",
                session.getUserId());
    }

    @Transactional
    public void startProtection(UUID userId) {

        log.info("PROTECTION_START | userId={}",
                userId);

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElse(new ActiveSafetySession());

        session.setUserId(userId);

        stateMachineService.transitionState(
                session,
                SessionStatus.ACTIVE,
                "PROTECTION_STARTED",
                "API"
        );

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

        session.setLastBluetoothSeenAt(LocalDateTime.now());

        activeSafetySessionRepository.save(session);
    }

    @Transactional
    public void ping(UUID userId) {

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    session.setLastPingTime(LocalDateTime.now());

                    session.setLastBluetoothSeenAt(
                            LocalDateTime.now()
                    );

                    if (session.getStatus() == SessionStatus.WARNING ||
                            session.getStatus() == SessionStatus.PAUSED_OFF_BODY) {

                        log.info("RECOVERY -> ACTIVE | userId={}",
                                userId);

                        stateMachineService.transitionState(
                                session,
                                SessionStatus.ACTIVE,
                                "HEARTBEAT_RECOVERY",
                                "HEARTBEAT_API"
                        );

                        session.setPauseType(null);

                        session.setAutoResumeAt(null);

                        session.setConfirmationStatus(
                                ConfirmationStatus.NONE
                        );

                        session.setWarningTriggeredAt(null);

                        session.setIsDeviceWorn(true);

                        session.setEmergencyTriggered(false);

                        session.setEmergencyContactNotified(false);
                    }

                    activeSafetySessionRepository.save(session);
                });
    }

    @Transactional
    public void stopProtection(UUID userId) {

        log.info("PROTECTION_STOP | userId={}",
                userId);

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    stateMachineService.transitionState(
                            session,
                            SessionStatus.ENDED,
                            "USER_STOPPED",
                            "API"
                    );

                    activeSafetySessionRepository.save(session);
                });
    }

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

        stateMachineService.transitionState(
                session,
                SessionStatus.PAUSED_MANUAL,
                "MANUAL_PAUSE",
                "API"
        );

        session.setPauseType(PauseType.MANUAL);

        session.setIsDeviceWorn(true);

        session.setAutoResumeAt(
                LocalDateTime.now().plusMinutes(minutes)
        );

        activeSafetySessionRepository.save(session);
    }

    @Transactional
    public void resumeProtection(UUID userId) {

        log.info("PROTECTION_RESUME | userId={}",
                userId);

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        stateMachineService.transitionState(
                session,
                SessionStatus.ACTIVE,
                "MANUAL_RESUME",
                "API"
        );

        session.setPauseType(null);

        session.setAutoResumeAt(null);

        session.setConfirmationStatus(
                ConfirmationStatus.NONE
        );

        session.setWarningTriggeredAt(null);

        session.setIsDeviceWorn(true);

        activeSafetySessionRepository.save(session);
    }

    @Transactional
    public void confirmUserSafe(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        log.info("USER_CONFIRMED_SAFE | userId={}",
                userId);

        stateMachineService.transitionState(
                session,
                SessionStatus.RECOVERY_PENDING,
                "USER_CONFIRMED_SAFE",
                "API"
        );

        session.setConfirmationStatus(
                ConfirmationStatus.SAFE_CONFIRMED
        );

        session.setWarningTriggeredAt(null);

        activeSafetySessionRepository.save(session);
    }

    @Transactional
    public void updateBluetoothPing(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn("SESSION_NOT_FOUND for bluetooth ping -> Creating | userId={}",
                                    userId);

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);
                            newSession.setStatus(SessionStatus.ACTIVE);
                            newSession.setSessionStartTime(LocalDateTime.now());
                            newSession.setEmergencyTriggered(false);
                            newSession.setEmergencyContactNotified(false);
                            newSession.setLastPingTime(LocalDateTime.now());
                            return activeSafetySessionRepository.save(newSession);
                        });

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        if (session.getStatus() == SessionStatus.WARNING) {

            log.info("📡 BLUETOOTH_RECOVERY | userId={}",
                    userId);

            stateMachineService.transitionState(
                    session,
                    SessionStatus.ACTIVE,
                    "BLUETOOTH_RECOVERY",
                    "DEVICE"
            );

            session.setConfirmationStatus(
                    ConfirmationStatus.NONE
            );

            session.setWarningTriggeredAt(null);
        }

        if (session.getStatus() == SessionStatus.PAUSED_OFF_BODY) {

            log.info("⌚ DEVICE_WORN_AGAIN | userId={}",
                    userId);

            stateMachineService.transitionState(
                    session,
                    SessionStatus.ACTIVE,
                    "DEVICE_WORN_AGAIN",
                    "DEVICE"
            );

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

    @Transactional
    public void updateVitals(UUID userId,
                             int heartRate,
                             int movementScore) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn(
                                    "SESSION_NOT_FOUND For update vitals -> Creating | userId={}",
                                    userId
                            );

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);
                            newSession.setStatus(SessionStatus.ACTIVE);
                            newSession.setSessionStartTime(LocalDateTime.now());
                            newSession.setEmergencyTriggered(false);
                            newSession.setEmergencyContactNotified(false);
                            newSession.setLastPingTime(LocalDateTime.now());

                            // Communication defaults
                            newSession.setCommunicationMode(
                                    CommunicationMode.PHONE_BLUETOOTH
                            );

                            newSession.setCommunicationFailureCount(0);

                            return activeSafetySessionRepository.save(
                                    newSession
                            );
                        });

        // =====================================
        // UPDATE VITALS
        // =====================================

        session.setLastPingTime(LocalDateTime.now());

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        session.setLastHeartRate(heartRate);

        session.setMovementScore(movementScore);

        log.info(
                "💓 VITALS | userId={} | HR={} | movement={}",
                userId,
                heartRate,
                movementScore
        );

        // =====================================
        // BLUETOOTH STATUS
        // =====================================

        boolean bluetoothConnected =
                session.getLastBluetoothSeenAt() != null;

        // =====================================
        // OFF BODY ANALYSIS
        // =====================================

        OffBodyAnalysisResult offBodyResult =
                offBodyIntelligenceService.analyze(session.getDeviceId(),
                        Boolean.TRUE.equals(
                                session.getIsDeviceWorn()
                        ),
                        heartRate,
                        movementScore,
                        bluetoothConnected
                );

        log.info(
                "OFF_BODY_ANALYSIS | type={} | risk={} | dangerous={}",
                offBodyResult.getEventType(),
                offBodyResult.getRiskScore(),
                offBodyResult.isDangerous()
        );
        // =====================================
        // BASE RISK SCORE
        // =====================================

        int riskScore =
                riskScoreCalculatorService.calculateRiskScore(
                        heartRate,
                        movementScore,
                        bluetoothConnected,
                        Boolean.TRUE.equals(
                                session.getIsDeviceWorn()
                        )
                );
        // =====================================
        // ADD OFF BODY RISK
        // =====================================
        riskScore += offBodyResult.getRiskScore();
        // Cap to 100
        riskScore = Math.min(100, riskScore);
        session.setRiskScore(riskScore);
        log.warn(
                "🚨 RISK_SCORE | userId={} | riskScore={}",
                userId,
                riskScore
        );
        // =====================================
        // OFF BODY EMERGENCY
        // =====================================

        if (offBodyResult.isDangerous()) {
            log.error(
                    "OFF_BODY_DANGER_DETECTED | type={}",
                    offBodyResult.getEventType()
            );
            forceEmergency(session);
            activeSafetySessionRepository.save(session);
            return;
        }
        // =====================================
        // RISK DECISION ENGINE
        // =====================================

        if (riskScore >= 70) {
            forceEmergency(session);
        } else if (riskScore >= 40) {
            if (session.getStatus() == SessionStatus.ACTIVE) {
                stateMachineService.transitionState(
                        session,
                        SessionStatus.SOFT_MONITORING,
                        "MEDIUM_RISK_DETECTED",
                        "RISK_ENGINE"
                );
            }
        } else {
            if (session.getStatus()
                    == SessionStatus.SOFT_MONITORING) {
                stateMachineService.transitionState(
                        session,
                        SessionStatus.ACTIVE,
                        "RISK_NORMALIZED",
                        "RISK_ENGINE"
                );
            }
        }
        activeSafetySessionRepository.save(session);
    }

    private void forceEmergency(ActiveSafetySession session) {

        if (Boolean.TRUE.equals(session.getEmergencyTriggered())) {
            return;
        }
        log.error("🚨 FORCE_SOS | userId={} | reason={}",
                session.getUserId(),
                "HEART_RATE_AND_MOVEMENT");
        CommunicationResult result =
                emergencyCommunicationService
                        .attemptCommunication(session);

        if (result == CommunicationResult.FAILED) {

            communicationFallbackService
                    .escalateCommunication(session);
        }
        sosTriggerService.triggerSosViaOutbox(session, "HEART_RATE_AND_MOVEMENT");
    }


    @Transactional
    public void markDeviceOffBody(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException("Session not found"));

        log.warn("📴 DEVICE_OFF_BODY | userId={}",
                userId);

        stateMachineService.transitionState(
                session,
                SessionStatus.PAUSED_OFF_BODY,
                "DEVICE_REMOVED",
                "DEVICE"
        );

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