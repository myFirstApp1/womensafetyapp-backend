package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActiveSafetySessionRepository extends JpaRepository<ActiveSafetySession, UUID> {
    
    List<ActiveSafetySession> findAllByIsProtectedTrue();
    
   // @Modifying
    //@Query("UPDATE ActiveSafetySession a SET a.isProtected = false WHERE a.lastPingTime < :threshold AND a.isProtected = true")
    //int deactivateStaleHeartbeats(LocalDateTime threshold);

    List<ActiveSafetySession> findByIsProtectedTrueAndLastPingTimeBefore(LocalDateTime threshold);

    List<ActiveSafetySession> findByIsProtectedTrue();

    List<ActiveSafetySession> findByIsProtectedTrueAndEmergencyTriggeredFalse();
}
