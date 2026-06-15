package com.womensafety.sosservice.service.tracking;

import com.womensafety.sosservice.domain.TrackingSession;
import com.womensafety.sosservice.dto.TrackingUpdateRequest;
import com.womensafety.sosservice.repository.TrackingSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingSessionRepository trackingSessionRepository;

    // =========================================
    // UPDATE LIVE LOCATION
    // =========================================

    @Transactional
    public void updateLocation(TrackingUpdateRequest request) {

        TrackingSession tracking = TrackingSession.builder()
                .userId(request.userId())
                .trackingId(request.trackingId())
                .latitude(BigDecimal.valueOf(request.latitude()))
                .longitude(BigDecimal.valueOf(request.longitude()))
                .accuracyMeters(request.accuracyMeters())
                .speed(request.speed())
                .recordedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        trackingSessionRepository.save(tracking);

        log.info(
                "📍 LIVE_TRACKING | trackingId={} | lat={} | lon={} | speed={}",
                request.trackingId(),
                request.latitude(),
                request.longitude(),
                request.speed()
        );
    }
    // =========================================
    // GET LATEST LOCATION
    // =========================================

    public TrackingSession getLatestLocation(String trackingId) {

        return trackingSessionRepository
                .findTopByTrackingIdOrderByRecordedAtDesc(trackingId)
                .orElseThrow(() ->
                        new RuntimeException("Tracking session not found"));
    }

    // =========================================
    // GET TRACKING HISTORY
    // =========================================

    public List<TrackingSession> getTrackingHistory(String trackingId) {

        return trackingSessionRepository
                .findTop100ByTrackingIdOrderByRecordedAtDesc(trackingId);
    }
}
