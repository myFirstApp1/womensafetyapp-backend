package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.FamilyIncidentDashboardResponse;
import com.womensafety.sosservice.service.tracking.FamilyDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyDashboardController {

    private final FamilyDashboardService service;

    @GetMapping("/dashboard")
    public FamilyIncidentDashboardResponse dashboard(
            @RequestParam String trackingId
    ) {

        return service.getDashboard(
                trackingId
        );
    }
}