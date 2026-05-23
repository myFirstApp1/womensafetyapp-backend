package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.PoliceIncidentPacket;
import com.womensafety.sosservice.service.PoliceIncidentPacketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/incidents")
public class IncidentController {

    private final PoliceIncidentPacketService
            policeIncidentPacketService;

    @GetMapping("/{trackingId}")
    public PoliceIncidentPacket getIncident(
            @PathVariable String trackingId
    ) {
        return policeIncidentPacketService
                .buildPacket(
                        trackingId
                );
    }
}