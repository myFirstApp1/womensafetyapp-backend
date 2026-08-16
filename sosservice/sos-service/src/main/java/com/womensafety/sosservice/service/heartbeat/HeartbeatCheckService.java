package com.womensafety.sosservice.service.heartbeat;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.*;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.SosOutboxRepository;
import com.womensafety.sosservice.service.OffBodyIntelligenceService;
import com.womensafety.sosservice.service.communication.CommunicationFallbackService;
import com.womensafety.sosservice.service.communication.EmergencyCommunicationService;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.risk.RiskScoreCalculatorService;
import com.womensafety.sosservice.service.sos.SosTriggerService;
import com.womensafety.sosservice.service.timeline.EmergencyTimelineService;
import com.womensafety.sosservice.statemachine.SessionStateMachineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartbeatCheckService implements IHeartbeatCheckService {

    private final ActiveSafetySessionRepository activeSafetySessionRepository;
    private final SosOutboxRepository sosOutboxRepository;
    private final SessionStateMachineService stateMachineService;
    private final RiskScoreCalculatorService riskScoreCalculatorService;
    private final CommunicationFallbackService communicationFallbackService;
    private final EmergencyCommunicationService emergencyCommunicationService;
    private final OffBodyIntelligenceService offBodyIntelligenceService;
    private final EmergencyTimelineService emergencyTimelineService;
    private final SosTriggerService sosTriggerService;
    private final SessionManager sessionManager;

    private static final int HEARTBEAT_TIMEOUT_MINUTES = 3;
    private static final int WARNING_TIMEOUT_SECONDS = 60;
    private static final int BLUETOOTH_TIMEOUT_MINUTES = 2;
    private static final int PAUSE_ESCALATION_MINUTES = 10;

    // =========================================================
    // HEARTBEAT SCHEDULER
    // =========================================================

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

                // =================================================
                // 1. AUTO RESUME
                // =================================================

                if (session.getAutoResumeAt() != null &&
                        session.getAutoResumeAt().isBefore(now)) {

                    log.info(
                            "AUTO_RESUME | userId={}",
                            session.getUserId()
                    );

                    stateMachineService.transitionState(
                            session,
                            SessionStatus.ACTIVE,
                            "AUTO_RESUME",
                            "SCHEDULER"
                    );

                    session.setPauseType(null);
                    session.setAutoResumeAt(null);

                    sessionManager.save(session);

                    continue;
                }

                // =================================================
                // 2. HEARTBEAT MISSING
                // ACTIVE -> WARNING
                // =================================================

                if (session.getStatus() == SessionStatus.ACTIVE &&
                        session.getLastPingTime() != null &&
                        session.getLastPingTime().isBefore(
                                heartbeatThreshold
                        )) {

                    log.warn(
                            "⚠️ WARNING_TRIGGER | heartbeat missing | userId={}",
                            session.getUserId()
                    );

                    moveToWarning(session, now);

                    continue;
                }

                // =================================================
                // 3. BLUETOOTH DISCONNECT
                // =================================================

                if (session.getStatus() == SessionStatus.ACTIVE &&
                        session.getLastBluetoothSeenAt() != null &&
                        session.getLastBluetoothSeenAt().isBefore(
                                bluetoothThreshold
                        )) {

                    log.warn(
                            "📡 DEVICE_DISCONNECTED | userId={}",
                            session.getUserId()
                    );

                    moveToWarning(session, now);

                    continue;
                }

                // =================================================
                // 4. USER CONFIRMED SAFE
                // WARNING -> ACTIVE
                // =================================================

                if (session.getStatus() == SessionStatus.WARNING &&
                        session.getConfirmationStatus()
                                == ConfirmationStatus.SAFE_CONFIRMED) {

                    log.info(
                            "USER_CONFIRMED_SAFE | userId={}",
                            session.getUserId()
                    );

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

                    sessionManager.save(session);

                    continue;
                }

                // =================================================
                // 5. WARNING -> SOS
                // =================================================

                if (session.getStatus() == SessionStatus.WARNING &&
                        session.getConfirmationStatus()
                                == ConfirmationStatus.PENDING &&
                        session.getWarningTriggeredAt() != null &&
                        session.getWarningTriggeredAt().isBefore(
                                warningThreshold
                        ) &&
                        !Boolean.TRUE.equals(
                                session.getEmergencyTriggered()
                        )) {

                    log.error(
                            "🚨 NO_RESPONSE -> SOS | userId={}",
                            session.getUserId()
                    );

                    session.setConfirmationStatus(
                            ConfirmationStatus.NO_RESPONSE
                    );

                    sosTriggerService.triggerSosViaOutbox(
                            session,
                            "NO_RESPONSE"
                    );

                    continue;
                }

                // =================================================
                // 6. PAUSED -> WARNING
                // =================================================

                if ((session.getStatus()
                        == SessionStatus.PAUSED_MANUAL ||
                        session.getStatus()
                                == SessionStatus.PAUSED_OFF_BODY) &&
                        session.getLastPingTime() != null &&
                        session.getLastPingTime().isBefore(
                                pauseThreshold
                        )) {

                    log.warn(
                            "⚠️ PAUSED_ESCALATION | userId={}",
                            session.getUserId()
                    );

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

    // =========================================================
    // MOVE TO WARNING
    // =========================================================

    private void moveToWarning(
            ActiveSafetySession session,
            LocalDateTime now
    ) {

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

        sessionManager.save(session);

        log.warn(
                "📲 PUSH_NOTIFICATION | Are you safe? | userId={}",
                session.getUserId()
        );
    }

    // =========================================================
    // START PROTECTION
    // =========================================================

    @Transactional
    public void startProtection(UUID userId) {

        log.info(
                "PROTECTION_START | userId={}",
                userId
        );

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElse(new ActiveSafetySession());

        session.setUserId(userId);

        if (session.getStatus() == SessionStatus.ACTIVE) {

            log.info(
                    "PROTECTION_ALREADY_ACTIVE | userId={}",
                    userId
            );

            return;
        }

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

        session.setSessionStartTime(
                LocalDateTime.now()
        );

        session.setLastPingTime(
                LocalDateTime.now()
        );

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        sessionManager.save(session);
    }

    // =========================================================
    // HEARTBEAT / PING
    // =========================================================

    @Transactional
    public void ping(UUID userId) {

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    session.setLastPingTime(
                            LocalDateTime.now()
                    );

                    session.setLastBluetoothSeenAt(
                            LocalDateTime.now()
                    );

                    if (session.getStatus()
                            == SessionStatus.WARNING ||
                            session.getStatus()
                                    == SessionStatus.PAUSED_OFF_BODY) {

                        log.info(
                                "RECOVERY -> ACTIVE | userId={}",
                                userId
                        );

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

                    sessionManager.save(session);
                });
    }

    // =========================================================
    // STOP PROTECTION
    // =========================================================

    @Transactional
    public void stopProtection(UUID userId) {

        log.info(
                "PROTECTION_STOP | userId={}",
                userId
        );

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {

                    stateMachineService.transitionState(
                            session,
                            SessionStatus.ENDED,
                            "USER_STOPPED",
                            "LOGOUT"
                    );

                    sessionManager.save(session);
                });
    }

    // =========================================================
    // PAUSE PROTECTION
    // =========================================================

    @Transactional
    public void pauseProtection(
            UUID userId,
            int minutes
    ) {

        log.info(
                "PROTECTION_PAUSE | userId={} | duration={} mins",
                userId,
                minutes
        );

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn(
                                    "SESSION_NOT_FOUND -> Creating | userId={}",
                                    userId
                            );

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);

                            newSession.setStatus(
                                    SessionStatus.ACTIVE
                            );

                            newSession.setSessionStartTime(
                                    LocalDateTime.now()
                            );

                            return sessionManager.saveAndReturn(
                                    newSession
                            );
                        });

        stateMachineService.transitionState(
                session,
                SessionStatus.PAUSED_MANUAL,
                "MANUAL_PAUSE",
                "API"
        );

        session.setPauseType(
                PauseType.MANUAL
        );

        session.setIsDeviceWorn(true);

        session.setAutoResumeAt(
                LocalDateTime.now().plusMinutes(minutes)
        );

        sessionManager.save(session);
    }

    // =========================================================
    // RESUME PROTECTION
    // =========================================================

    @Transactional
    public void resumeProtection(UUID userId) {

        log.info(
                "PROTECTION_RESUME | userId={}",
                userId
        );

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                )
                        );

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

        sessionManager.save(session);
    }

    // =========================================================
    // CONFIRM USER SAFE
    // =========================================================

    @Transactional
    public void confirmUserSafe(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                )
                        );

        log.info(
                "USER_CONFIRMED_SAFE | userId={}",
                userId
        );

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

    // =========================================================
    // BLUETOOTH PING
    // =========================================================

    @Transactional
    public void updateBluetoothPing(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseGet(() -> {

                            log.warn(
                                    "SESSION_NOT_FOUND for bluetooth ping -> Creating | userId={}",
                                    userId
                            );

                            ActiveSafetySession newSession =
                                    new ActiveSafetySession();

                            newSession.setUserId(userId);

                            newSession.setStatus(
                                    SessionStatus.ACTIVE
                            );

                            newSession.setSessionStartTime(
                                    LocalDateTime.now()
                            );

                            newSession.setEmergencyTriggered(
                                    false
                            );

                            newSession.setEmergencyContactNotified(
                                    false
                            );

                            newSession.setLastPingTime(
                                    LocalDateTime.now()
                            );

                            return sessionManager.saveAndReturn(
                                    newSession
                            );
                        });

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        if (session.getStatus()
                == SessionStatus.WARNING) {

            log.info(
                    "📡 BLUETOOTH_RECOVERY | userId={}",
                    userId
            );

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

        if (session.getStatus()
                == SessionStatus.PAUSED_OFF_BODY) {

            log.info(
                    "⌚ DEVICE_WORN_AGAIN | userId={}",
                    userId
            );

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

        sessionManager.save(session);

        log.info(
                "📡 BLUETOOTH_PING | userId={}",
                userId
        );
    }

    // =========================================================
    // UPDATE VITALS
    // V1 JAVA PROTECTION ENGINE
    // =========================================================

    @Transactional
    public void updateVitals(
            UUID userId,
            int heartRate,
            int hrv,
            double movement,
            double speed,
            double accelX,
            double accelY,
            double accelZ,
            double gyroX,
            double gyroY,
            double gyroZ,
            int battery,
            int worn
    ) {

        // Temporary compatibility with existing RiskScoreCalculator
        int movementScore = (int) movement;

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

                            newSession.setStatus(
                                    SessionStatus.ACTIVE
                            );

                            newSession.setSessionStartTime(
                                    LocalDateTime.now()
                            );

                            newSession.setEmergencyTriggered(
                                    false
                            );

                            newSession.setEmergencyContactNotified(
                                    false
                            );

                            newSession.setLastPingTime(
                                    LocalDateTime.now()
                            );

                            newSession.setCommunicationMode(
                                    CommunicationMode.PHONE_BLUETOOTH
                            );

                            newSession.setCommunicationFailureCount(
                                    0
                            );

                            return sessionManager.saveAndReturn(
                                    newSession
                            );
                        });

        // =====================================================
        // UPDATE VITALS
        // =====================================================

        session.setLastPingTime(
                LocalDateTime.now()
        );

        session.setLastBluetoothSeenAt(
                LocalDateTime.now()
        );

        session.setLastHeartRate(
                heartRate
        );

        session.setMovementScore(
                movementScore
        );

        log.info(
                "💓 VITALS | userId={} | HR={} | HRV={} | movement={}",
                userId,
                heartRate,
                hrv,
                movementScore
        );

        // =====================================================
        // BLUETOOTH STATUS
        // =====================================================

        boolean bluetoothConnected =
                session.getLastBluetoothSeenAt() != null
                        && session.getLastBluetoothSeenAt()
                        .isAfter(
                                LocalDateTime.now()
                                        .minusMinutes(
                                                BLUETOOTH_TIMEOUT_MINUTES
                                        )
                        );

        // =====================================================
        // OFF BODY ANALYSIS
        // =====================================================

        OffBodyAnalysisResult offBodyResult =
                offBodyIntelligenceService.analyze(
                        session.getDeviceId(),
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

        // =====================================================
        // V1 JAVA RISK SCORE
        // =====================================================

        int riskScore =
                riskScoreCalculatorService.calculateRiskScore(
                        heartRate,
                        movementScore,
                        bluetoothConnected,
                        Boolean.TRUE.equals(
                                session.getIsDeviceWorn()
                        )
                );

        log.info(
                "V1_RISK_SCORE | userId={} | riskScore={}",
                userId,
                riskScore
        );

        // =====================================================
        // ADD OFF-BODY RISK
        // =====================================================

        riskScore += offBodyResult.getRiskScore();

        // Cap risk score at 100
        riskScore = Math.min(
                100,
                riskScore
        );

        session.setRiskScore(
                riskScore
        );

        log.warn(
                "🚨 RISK_SCORE | userId={} | riskScore={}",
                userId,
                riskScore
        );

        // =====================================================
        // OFF-BODY EMERGENCY
        // =====================================================

        if (offBodyResult.isDangerous()) {

            log.error(
                    "OFF_BODY_DANGER_DETECTED | type={}",
                    offBodyResult.getEventType()
            );

            forceEmergency(session);

            sessionManager.save(session);

            return;
        }

        // =====================================================
        // V1 RISK DECISION ENGINE
        // =====================================================

        if (riskScore >= 70) {

            forceEmergency(session);

        } else if (riskScore >= 40) {

            if (session.getStatus()
                    == SessionStatus.ACTIVE) {

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

        sessionManager.save(
                session
        );
    }

    // =========================================================
    // FORCE EMERGENCY
    // =========================================================

    private void forceEmergency(
            ActiveSafetySession session
    ) {

        if (Boolean.TRUE.equals(
                session.getEmergencyTriggered()
        )) {

            return;
        }

        log.error(
                "🚨 FORCE_SOS | userId={} | reason={}",
                session.getUserId(),
                "HEART_RATE_AND_MOVEMENT"
        );

        CommunicationResult result =
                emergencyCommunicationService
                        .attemptCommunication(
                                session
                        );

        if (result == CommunicationResult.FAILED) {

            communicationFallbackService
                    .escalateCommunication(
                            session
                    );
        }

        sosTriggerService.triggerSosViaOutbox(
                session,
                "HEART_RATE_AND_MOVEMENT"
        );
    }

    // =========================================================
    // MARK DEVICE OFF BODY
    // =========================================================

    @Transactional
    public void markDeviceOffBody(UUID userId) {

        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                )
                        );

        log.warn(
                "📴 DEVICE_OFF_BODY | userId={}",
                userId
        );

        stateMachineService.transitionState(
                session,
                SessionStatus.PAUSED_OFF_BODY,
                "DEVICE_REMOVED",
                "DEVICE"
        );

        session.setPauseType(
                PauseType.OFF_BODY
        );

        session.setIsDeviceWorn(
                false
        );

        sessionManager.save(
                session
        );
    }

    // =========================================================
    // GET PROTECTED USERS
    // =========================================================

    public List<ActiveSafetySession> getProtectedUsers() {

        return activeSafetySessionRepository
                .findAllByStatus(
                        SessionStatus.ACTIVE
                );
    }

    // =========================================================
    // CHECK USER PROTECTION STATUS
    // =========================================================

    public boolean isUserProtected(UUID userId) {

        return activeSafetySessionRepository
                .findById(userId)
                .map(session ->
                        session.getStatus()
                                == SessionStatus.ACTIVE
                )
                .orElse(false);
    }

    // =========================================================
    // GET STALE USERS
    // =========================================================

    public List<ActiveSafetySession> getStaleUsers() {

        LocalDateTime threshold =
                LocalDateTime.now()
                        .minusMinutes(
                                HEARTBEAT_TIMEOUT_MINUTES
                        );

        return activeSafetySessionRepository
                .findByStatusAndLastPingTimeBefore(
                        SessionStatus.ACTIVE,
                        threshold
                );
    }
}