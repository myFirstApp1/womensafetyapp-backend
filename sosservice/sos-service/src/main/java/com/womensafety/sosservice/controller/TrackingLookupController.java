package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.TrackingIdResponse;
import com.womensafety.sosservice.service.tracking.TrackingSessionLookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingLookupController {

    private final TrackingSessionLookupService trackingSessionLookupService;

    @GetMapping("/tracking-id/{userId}")
    public ResponseEntity<TrackingIdResponse> getTrackingId(
            @PathVariable UUID userId
    ) {

        return ResponseEntity.ok(
                trackingSessionLookupService.getTrackingId(
                        userId
                )
        );
    }
}