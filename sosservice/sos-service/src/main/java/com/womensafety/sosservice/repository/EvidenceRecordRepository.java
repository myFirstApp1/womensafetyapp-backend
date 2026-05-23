package com.womensafety.sosservice.repository;

import com.womensafety.sosservice.domain.EvidenceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRecordRepository
        extends JpaRepository<EvidenceRecord, Long> {

    List<EvidenceRecord>
    findByTrackingId(
            String trackingId
    );

    List<EvidenceRecord>
    findByTrackingIdOrderByUploadedAtDesc(
            String trackingId
    );
}