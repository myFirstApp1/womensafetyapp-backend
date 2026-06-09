package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.IncidentDispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentDispatchRepository
        extends JpaRepository<IncidentDispatch, Long> {

    List<IncidentDispatch>
    findByTrackingId(
            String trackingId
    );
}

