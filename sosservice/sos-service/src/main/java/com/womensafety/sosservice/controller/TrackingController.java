package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.TrackingSession;
import com.womensafety.sosservice.dto.TrackingUpdateRequest;
import com.womensafety.sosservice.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}