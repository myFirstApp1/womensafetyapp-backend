package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrackingSessionRepository
        extends JpaRepository<TrackingSession, Long> {

    Optional<TrackingSession>
    findTopByTrackingIdOrderByRecordedAtDesc(String trackingId);

    List<TrackingSession>
    findTop100ByTrackingIdOrderByRecordedAtDesc(String trackingId);

    Optional<TrackingSession>
    findTopByUserIdOrderByRecordedAtDesc(UUID userId);

    @Modifying
    @Query("""
            UPDATE TrackingSession t
            SET t.isActive = false
            WHERE t.trackingId = :trackingId
            """)
    void deactivateTracking(@Param("trackingId") String trackingId);
}