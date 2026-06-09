package com.womensafety.sosservice.service.timeline;

import com.womensafety.sosservice.dto.EvidenceResponse;
import com.womensafety.sosservice.repository.EvidenceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvidenceRetrievalService {

    private final EvidenceRecordRepository repository;

    public List<EvidenceResponse> getEvidence(
            String trackingId
    ) {

        return repository
                .findByTrackingIdOrderByUploadedAtDesc(
                        trackingId
                )
                .stream()
                .map(record ->
                        EvidenceResponse.builder()
                                .evidenceId(
                                        record.getEvidenceId()
                                )
                                .fileType(
                                        record.getFileType().name()
                                )
                                .storageUrl(
                                        record.getStorageUrl()
                                )
                                .hashValue(
                                        record.getHashValue()
                                )
                                .uploadedAt(
                                        record.getUploadedAt()
                                )
                                .uploadedBy(
                                        record.getUploadedBy()
                                )
                                .build()
                )
                .toList();
    }
}
