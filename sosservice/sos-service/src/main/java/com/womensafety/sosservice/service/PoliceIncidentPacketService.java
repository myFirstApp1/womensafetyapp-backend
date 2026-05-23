package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.EmergencyTimeline;
import com.womensafety.sosservice.domain.EvidenceRecord;
import com.womensafety.sosservice.dto.PoliceIncidentPacket;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import com.womensafety.sosservice.repository.EvidenceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PoliceIncidentPacketService {

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;

    private final EmergencyTimelineRepository
            emergencyTimelineRepository;

    private final EvidenceRecordRepository
            evidenceRecordRepository;

    public PoliceIncidentPacket buildPacket(
            String trackingId
    ) {

        ActiveSafetySession session =
                activeSafetySessionRepository
                        .findByTrackingId(
                                trackingId
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "TrackingId not found"
                                )
                        );

        List<EmergencyTimeline> timeline =
                emergencyTimelineRepository
                        .findByTrackingIdOrderByCreatedAtAsc(
                                trackingId
                        );

        List<EvidenceRecord> evidence =
                evidenceRecordRepository
                        .findByTrackingIdOrderByUploadedAtDesc(
                                trackingId
                        );

        List<String> timelineEvents =
                timeline.stream()
                        .map(
                                t -> t.getCreatedAt()
                                        + " | "
                                        + t.getEventType()
                                        + " | "
                                        + t.getEventData()
                        )
                        .collect(
                                Collectors.toList()
                        );

        List<String> evidenceUrls =
                evidence.stream()
                        .map(
                                EvidenceRecord::getStorageUrl
                        )
                        .collect(
                                Collectors.toList()
                        );

        return PoliceIncidentPacket.builder()
                .trackingId(
                        session.getTrackingId()
                )
                .userId(
                        session.getUserId()
                )
                .riskScore(
                        session.getRiskScore()
                )
                .status(
                        session.getStatus() == null
                                ? null
                                : session.getStatus().name()
                )
                .communicationMode(
                        session.getCommunicationMode() == null
                                ? null
                                : session.getCommunicationMode().name()
                )
                .latitude(
                        session.getLastLatitude() == null
                                ? null
                                : session.getLastLatitude().doubleValue()
                )
                .longitude(
                        session.getLastLongitude() == null
                                ? null
                                : session.getLastLongitude().doubleValue()
                )
                .incidentTime(
                        session.getSessionStartTime()
                )
                .timelineEvents(
                        timelineEvents
                )
                .evidenceUrls(
                        evidenceUrls
                )
                .build();
    }
}