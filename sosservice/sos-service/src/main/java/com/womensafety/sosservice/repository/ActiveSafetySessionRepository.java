package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActiveSafetySessionRepository extends JpaRepository<ActiveSafetySession, UUID> {
    
    List<ActiveSafetySession> findAllByStatus(SessionStatus status);

    // Get stale users (heartbeat missing)
    List<ActiveSafetySession> findByStatusAndLastPingTimeBefore(
            SessionStatus status,
            LocalDateTime time
    );
    List<ActiveSafetySession> findByStatusIn(List<SessionStatus> statuses);
    List<ActiveSafetySession> findByStatus(SessionStatus status);

    Optional<ActiveSafetySession>
    findByDeviceId(String deviceId);

    Optional<ActiveSafetySession> findByTrackingId(String trackingId);

}
