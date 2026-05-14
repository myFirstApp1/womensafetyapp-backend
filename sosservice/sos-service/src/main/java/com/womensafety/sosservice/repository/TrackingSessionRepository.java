package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackingSessionRepository
        extends JpaRepository<TrackingSession, Long> {

    Optional<TrackingSession>
    findTopByTrackingIdOrderByRecordedAtDesc(String trackingId);

    List<TrackingSession>
    findTop100ByTrackingIdOrderByRecordedAtDesc(String trackingId);
}