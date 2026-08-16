package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.IncidentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IncidentEventRepository
        extends JpaRepository<IncidentEvent, UUID> {

    List<IncidentEvent> findByIncidentIdOrderByCreatedAtAsc(UUID incidentId);

    List<IncidentEvent> findByUserIdOrderByCreatedAtDesc(UUID userId);



}
