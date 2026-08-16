package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.Incident;
import com.womensafety.sosservice.domain.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository
        extends JpaRepository<Incident, UUID> {

    Optional<Incident> findTopByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    List<Incident> findByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    Optional<Incident> findByTrackingId(
            String trackingId
    );

    List<Incident> findByStatus(
            IncidentStatus status
    );

    boolean existsByUserIdAndStatus(
            UUID userId,
            IncidentStatus status
    );

    Optional<Incident> findTopByUserIdAndStatusInOrderByCreatedAtDesc(
            UUID userId,
            List<IncidentStatus> statuses
    );

    List<Incident> findTop50ByUserIdOrderByCreatedAtDesc(
            UUID userId
    );

}
