package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.GpsStatus;
import com.womensafety.sosservice.domain.enums.HeartbeatLossType;
import com.womensafety.sosservice.domain.enums.TamperEventType;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.EmergencyTimelineRepository;
import com.womensafety.sosservice.service.*;
import com.womensafety.sosservice.service.communication.CommunicationFallbackService;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.location.GpsIntelligenceService;
import com.womensafety.sosservice.service.risk.RiskDecisionEngine;
import com.womensafety.sosservice.service.timeline.EmergencyOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final ActiveSafetySessionRepository activeSafetySessionRepository;
        private final SessionManager sessionManager;
    private final CommunicationFallbackService communicationFallbackService;
    private final GpsIntelligenceService gpsIntelligenceService;
    private final RiskDecisionEngine riskDecisionEngine;
    private final EmergencyOrchestratorService emergencyOrchestratorService;
    private final EmergencyTimelineRepository emergencyTimelineRepository;


    @PostMapping("/test/fallback/{userId}")
    public String testFallback(
            @PathVariable UUID userId
    ) {
        ActiveSafetySession session =
                activeSafetySessionRepository.findById(userId)
                        .orElseThrow();
        communicationFallbackService
                .escalateCommunication(session);
        sessionManager.save(session);
        return "Fallback Triggered";
    }

    @PostMapping("/test/gps")
    public GpsAnalysisResult testGps(
            @RequestParam String deviceId
    ) {
        return gpsIntelligenceService
                .analyze(deviceId);
    }

    @PostMapping("/test/risk")
    public RiskDecisionResult testRisk() {
        TamperAnalysisResult tamper =
                new TamperAnalysisResult(
                        TamperEventType.STRAP_CUT,
                        100,
                        true
                );
        HeartbeatLossResult heartbeat =
                new HeartbeatLossResult(
                        HeartbeatLossType.SIGNAL_LOST,
                        40,
                        false
                );
        GpsAnalysisResult gps =
                new GpsAnalysisResult(
                        GpsStatus.STATIONARY_LONG_TIME,
                        20,
                        "User stopped"
                );
        return riskDecisionEngine.evaluate(
                tamper,
                heartbeat,
                gps
        );
    }
    @PostMapping("/test/emergency")
    public String testEmergency(
            @RequestParam String deviceId
    ) {
        ActiveSafetySession session =
                activeSafetySessionRepository
                        .findByDeviceId(deviceId)
                        .orElseThrow();
        TamperAnalysisResult tamper =
                new TamperAnalysisResult(
                        TamperEventType.STRAP_CUT,
                        100,
                        true
                );
        HeartbeatLossResult heartbeat =
                new HeartbeatLossResult(
                        HeartbeatLossType.SIGNAL_LOST,
                        40,
                        false
                );
        GpsAnalysisResult gps =
                new GpsAnalysisResult(
                        GpsStatus.STATIONARY_LONG_TIME,
                        20,
                        "Stopped"
                );
        emergencyOrchestratorService
                .evaluateAndTrigger(
                        session,
                        tamper,
                        heartbeat,
                        gps
                );

        return "Emergency Evaluation Completed";
    }

    @GetMapping("/timeline/{trackingId}")
    public List<EmergencyTimeline> getTimeline(
            @PathVariable String trackingId
    ) {
        return emergencyTimelineRepository
                .findByTrackingIdOrderByCreatedAtAsc(
                        trackingId
                );
    }


}
