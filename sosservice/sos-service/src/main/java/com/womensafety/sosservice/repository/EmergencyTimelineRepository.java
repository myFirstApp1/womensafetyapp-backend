package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.EmergencyTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyTimelineRepository
        extends JpaRepository<EmergencyTimeline, Long> {

    List<EmergencyTimeline>
    findByTrackingIdOrderByCreatedAtAsc(
            String trackingId
    );
}