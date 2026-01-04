package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.kafka.NotificationProducer;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class HeartbeatCheckService {

    private final ActiveSafetySessionRepository activeSafetySessionRepository;
    private final NotificationProducer notificationProducer;

    public HeartbeatCheckService(ActiveSafetySessionRepository activeSafetySessionRepository, NotificationProducer notificationProducer) {
        this.activeSafetySessionRepository = activeSafetySessionRepository;
        this.notificationProducer = notificationProducer;
    }

    /**
     * Scheduled task to check heartbeats every 60 seconds.
     * If a user hasn't pinged within 3 minutes and is still protected,
     * trigger emergency workflow (Scenario 7: phone broken).
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkHeartbeats() {
        log.debug("HEARTBEAT_CHECK | Starting scheduled heartbeat check");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(3);
        try {
        List<ActiveSafetySession> staleSessions =
                activeSafetySessionRepository.findByIsProtectedTrueAndLastPingTimeBefore(threshold);
            if (!staleSessions.isEmpty()) {
                log.info("HEARTBEAT_CHECK | Found {} stale protected sessions (threshold: 3 minutes)",
                        staleSessions.size());
                for (ActiveSafetySession session : staleSessions) {
                    UUID userId = session.getUserId();
                    log.warn("HEARTBEAT_CHECK | Triggering emergency for userId={} (Scenario 7: phone broken)",
                            userId);
                    // Trigger emergency workflow
                    try {
                        // 🔥 PREVENT DUPLICATE SOS
                        if (session.isProtected() && !session.isEmergencyTriggered()) {

                            String location = "UNKNOWN";
                            if (session.getLastLatitude() != null && session.getLastLongitude() != null) {
                                location = session.getLastLatitude() + "," + session.getLastLongitude();
                            }
                            log.warn("HEARTBEAT_CHECK | Triggering SOS for userId={} at location={}",
                                    session.getUserId(), location);
                            // 🚨 Trigger SOS
                            notificationProducer.sendAutomaticSOS(
                                    session.getUserId().toString(),
                                    location
                            );
                            // ✅ Update state
                            session.setEmergencyTriggered(true);
                            session.setEmergencyContactNotified(true);
                            session.setProtected(false);
                            activeSafetySessionRepository.save(session);
                            log.info("HEARTBEAT_CHECK | SOS triggered successfully for userId={}",
                                    session.getUserId());
                        }
                    } catch (Exception e) {
                        log.error("HEARTBEAT_CHECK | Error triggering emergency for userId={}", userId, e);
                    }
                }
            } else {
                log.debug("HEARTBEAT_CHECK | No stale protected sessions found");
            }
        } catch (Exception e) {
            log.error("HEARTBEAT_CHECK | Error during heartbeat check", e);
        }
    }

    /**
     * Start a new protection session for a user.
     */
    @Transactional
    public void startProtection(UUID userId) {
        log.info("PROTECTION_START | userId={}", userId);

        ActiveSafetySession session = activeSafetySessionRepository
                .findById(userId)
                .orElse(new ActiveSafetySession());
        session.setUserId(userId);
        session.setProtected(true);
        session.setEmergencyTriggered(false);
        session.setEmergencyContactNotified(false);
        session.setSessionStartTime(LocalDateTime.now());
        session.setLastPingTime(LocalDateTime.now());
        activeSafetySessionRepository.save(session);
    }
    /**
     * Update heartbeat for an active protection session.
     */
    @Transactional
    public void ping(UUID userId, Integer battery, BigDecimal lat, BigDecimal lon) {

        activeSafetySessionRepository.findById(userId)
                .ifPresentOrElse(session -> {

                    session.setLastPingTime(LocalDateTime.now());

                    if (battery != null) session.setBatteryLevel(battery);
                    if (lat != null) session.setLastLatitude(lat);
                    if (lon != null) session.setLastLongitude(lon);
                    activeSafetySessionRepository.save(session);
                },() -> log.warn("HEARTBEAT_PING | Session not found for userId={}", userId)
                );
    }

    /**
     * Stop protection session for a user.
     */
    @Transactional
    public void stopProtection(UUID userId) {
        log.info("PROTECTION_STOP | userId={}", userId);

        activeSafetySessionRepository.findById(userId)
                .ifPresent(session -> {
                    session.setProtected(false);
                    activeSafetySessionRepository.save(session);
                });
    }

    /**
     * Get all currently protected users.
     */
    public List<ActiveSafetySession> getProtectedUsers() {
        return activeSafetySessionRepository.findAllByIsProtectedTrue();
    }

    /**
     * Check if a user is currently protected.
     */
    public boolean isUserProtected(UUID userId) {
        return activeSafetySessionRepository.findById(userId)
                .map(ActiveSafetySession::isProtected)
                .orElse(false);
    }

    public List<ActiveSafetySession> getStaleUsers() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(3);
        return activeSafetySessionRepository.findByIsProtectedTrueAndLastPingTimeBefore(threshold);
    }

}
