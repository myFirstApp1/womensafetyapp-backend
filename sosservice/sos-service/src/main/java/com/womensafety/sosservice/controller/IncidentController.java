package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.UpdateIncidentStatusRequest;
import com.womensafety.sosservice.dto.IncidentEventResponse;
import com.womensafety.sosservice.dto.IncidentRequest;
import com.womensafety.sosservice.dto.IncidentResponse;
import com.womensafety.sosservice.service.incident.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(
            @Valid @RequestBody IncidentRequest request) {

        IncidentResponse response =
                incidentService.createIncident(
                        request.userId(),
                        request.trackingId(),
                        request.triggerType(),
                        request.riskScore(),
                        request.latitude(),
                        request.longitude(),
                        request.incidentSource()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{incidentId}")
    public ResponseEntity<IncidentResponse> getIncident(
            @PathVariable UUID incidentId) {

        return ResponseEntity.ok(
                incidentService.getIncident(incidentId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IncidentResponse>> getUserIncidents(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                incidentService.getUserIncidents(userId)
        );
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<IncidentResponse> getActiveIncident(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                incidentService.getActiveIncident(userId)
        );
    }

    @PutMapping("/{incidentId}/status")
    public ResponseEntity<IncidentResponse> updateStatus(
            @PathVariable UUID incidentId,
            @RequestBody UpdateIncidentStatusRequest request) {

        return ResponseEntity.ok(
                incidentService.updateStatus(
                        incidentId,
                        request.status()
                )
        );
    }

    @GetMapping("/{incidentId}/timeline")
    public ResponseEntity<List<IncidentEventResponse>> getTimeline(
            @PathVariable UUID incidentId) {

        return ResponseEntity.ok(
                incidentService.getTimeline(incidentId)
        );
    }
}