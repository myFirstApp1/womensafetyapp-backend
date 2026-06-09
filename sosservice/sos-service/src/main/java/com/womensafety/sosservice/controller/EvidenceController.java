package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.EvidenceRecord;
import com.womensafety.sosservice.dto.EvidenceRequest;
import com.womensafety.sosservice.dto.EvidenceResponse;
import com.womensafety.sosservice.repository.EvidenceRecordRepository;
import com.womensafety.sosservice.service.EvidenceRetrievalService;
import com.womensafety.sosservice.service.EvidenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EvidenceController {
    private final EvidenceService evidenceService;
    private final EvidenceRecordRepository evidenceRecordRepository;
    private final EvidenceRetrievalService service;

    @PostMapping("/evidence")
    public EvidenceRecord uploadEvidence(
            @RequestBody EvidenceRequest request
    ) {
        return evidenceService.saveEvidence(

                request.getTrackingId(),

                request.getFileType(),

                request.getStorageUrl(),

                request.getHashValue()
        );
    }

    @GetMapping("/evidence/{trackingId}")
    public List<EvidenceRecord> getEvidence(
            @PathVariable String trackingId
    ) {

        return evidenceRecordRepository
                .findByTrackingIdOrderByUploadedAtDesc(
                        trackingId
                );
    }

    @GetMapping("/api/evidence")
    public List<EvidenceResponse> getEvidence1(
            @RequestParam String trackingId
    ) {

        return service.getEvidence(
                trackingId
        );
    }
}
