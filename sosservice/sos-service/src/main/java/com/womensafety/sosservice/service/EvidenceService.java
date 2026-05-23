package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.EvidenceRecord;
import com.womensafety.sosservice.domain.EvidenceType;
import com.womensafety.sosservice.repository.EvidenceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenceService {

    private final EvidenceRecordRepository repository;

    public EvidenceRecord saveEvidence(

            String trackingId,

            EvidenceType fileType,

            String storageUrl,

            String hashValue
    ) {

        EvidenceRecord record =
                EvidenceRecord.builder()
                        .evidenceId(
                                UUID.randomUUID().toString()
                        )
                        .trackingId(
                                trackingId
                        )
                        .fileType(
                                fileType
                        )
                        .storageUrl(
                                storageUrl
                        )
                        .hashValue(
                                hashValue
                        )
                        .build();

        return repository.save(record);
    }
}