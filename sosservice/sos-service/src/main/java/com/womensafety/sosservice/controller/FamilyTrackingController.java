package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.FamilyTrackingResponse;
import com.womensafety.sosservice.service.tracking.FamilyTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/family")
public class FamilyTrackingController {

    private final FamilyTrackingService
            trackingService;

    @GetMapping("/{trackingId}")
    public FamilyTrackingResponse getTracking(
            @PathVariable String trackingId
    ) {

        return trackingService.getTracking(
                trackingId
        );
    }
}