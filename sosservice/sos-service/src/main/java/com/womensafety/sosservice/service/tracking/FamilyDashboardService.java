package com.womensafety.sosservice.service.tracking;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.dto.FamilyIncidentDashboardResponse;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.EvidenceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FamilyDashboardService {

    private final ActiveSafetySessionRepository sessionRepository;

    private final EvidenceRecordRepository evidenceRepository;

    public FamilyIncidentDashboardResponse getDashboard(
            String trackingId
    ) {

        ActiveSafetySession session =
                sessionRepository
                        .findByTrackingId(
                                trackingId
                        )
                        .orElseThrow();

        int evidenceCount =
                evidenceRepository
                        .findByTrackingId(
                                trackingId
                        )
                        .size();

        return FamilyIncidentDashboardResponse
                .builder()
                .trackingId(
                        trackingId
                )
                .riskScore(
                        session.getRiskScore()
                )
                .status(
                        session.getStatus().name()
                )
                .communicationMode(
                        session.getCommunicationMode().name()
                )
                .latitude(
                        session.getLastLatitude() == null
                                ? null
                                : session.getLastLatitude()
                )
                .longitude(
                        session.getLastLongitude() == null
                                ? null
                                : session.getLastLongitude()
                )
                .evidenceCount(
                        evidenceCount
                )
                /*.lastUpdateTime(
                        session.getLastPingTime()
                )*/
                .build();
    }
}
