package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.TrackingSession;
import com.womensafety.sosservice.dto.ActiveTrackingResponse;
import com.womensafety.sosservice.dto.TrackingUpdateRequest;

import com.womensafety.sosservice.service.tracking.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    // =========================================
    // UPDATE LOCATION
    // =========================================

    @PostMapping("/update")
    public ResponseEntity<String> updateLocation(
            @RequestBody TrackingUpdateRequest request) {

        trackingService.updateLocation(request);

        return ResponseEntity.ok("Tracking updated");
    }

    // =========================================
    // GET LATEST LOCATION
    // =========================================

    @GetMapping("/latest/{trackingId}")
    public ResponseEntity<TrackingSession> getLatestLocation(
            @PathVariable String trackingId) {

        return ResponseEntity.ok(
                trackingService.getLatestLocation(trackingId)
        );
    }

    // =========================================
    // GET TRACKING HISTORY
    // =========================================

    @GetMapping("/history/{trackingId}")
    public ResponseEntity<List<TrackingSession>> getTrackingHistory(
            @PathVariable String trackingId) {

        return ResponseEntity.ok(
                trackingService.getTrackingHistory(trackingId)
        );
    }

    @GetMapping("/active/{userId}")
    public ResponseEntity<ActiveTrackingResponse> getActiveTracking(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                trackingService.getActiveTracking(userId)
        );
    }

    @PostMapping("/stop/{trackingId}")
    public ResponseEntity<Void> stopTracking(
            @PathVariable String trackingId) {

        trackingService.stopTracking(trackingId);

        return ResponseEntity.ok().build();
    }
}