package com.womensafety.sosservice.service;

import com.womensafety.sosservice.ai.decision.ProtectionDecision;
import com.womensafety.sosservice.ai.dto.PredictionResponse;
import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.PreAlertStatus;
import com.womensafety.sosservice.dto.HeartbeatPacket;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.LocationHistoryRepository;
import com.womensafety.sosservice.repository.RegisteredDeviceRepository;
import com.womensafety.sosservice.service.core.SessionManager;
import com.womensafety.sosservice.service.incident.IncidentResponseService;
import com.womensafety.sosservice.service.location.GpsIntelligenceService;
import com.womensafety.sosservice.service.sensor.AiSensorRulesService;
import com.womensafety.sosservice.service.sensor.SensorFusionOrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class WearableHeartbeatService {

    private final RegisteredDeviceRepository repository;
    private final ActiveSafetySessionRepository activeSafetySessionRepository;
    private final LocationHistoryRepository locationHistoryRepository;

    /*
     * V1 Java sensor/rule engine.
     * Despite the existing class name "AiSensorRulesService",
     * this is local Java rule evaluation and does not require
     * the external Python AI service.
     */
    private final AiSensorRulesService aiSensorRulesService;

    private final IncidentResponseService incidentResponseService;

    private final SensorFusionOrchestratorService
            sensorFusionOrchestratorService;

    private final OffBodyIntelligenceService
            offBodyIntelligenceService;

    private final GpsIntelligenceService
            gpsIntelligenceService;

    private final SessionManager sessionManager;


    public void processHeartbeat(
            HeartbeatPacket packet
    ) {

        ActiveSafetySession session =
                activeSafetySessionRepository
                        .findByDeviceId(
                                packet.getDeviceId()
                        )
                        .orElse(null);

        if (session != null) {

            // =====================================================
            // UPDATE ACTIVE SAFETY SESSION
            // =====================================================

            if (session.getDeviceId() == null) {

                session.setDeviceId(
                        packet.getDeviceId()
                );
            }

            session.setLastLatitude(
                    BigDecimal.valueOf(
                            packet.getLatitude()
                    )
            );

            session.setLastLongitude(
                    BigDecimal.valueOf(
                            packet.getLongitude()
                    )
            );

            session.setBatteryLevel(
                    packet.getBatteryLevel()
            );

            session.setLastHeartRate(
                    packet.getHeartRate()
            );

            session.setMovementScore(
                    (int) Math.round(
                            packet.getMovement()
                    )
            );

            session.setIsDeviceWorn(
                    packet.getDeviceWorn()
            );

            if (Boolean.TRUE.equals(
                    packet.getBluetoothConnected()
            )) {

                session.setLastBluetoothSeenAt(
                        LocalDateTime.now()
                );
            }

            session.setLastPingTime(
                    LocalDateTime.now()
            );

            sessionManager.save(
                    session
            );


            // =====================================================
            // PRE-ALERT STATUS
            // =====================================================

            boolean preAlertActive =
                    session.getPreAlertStatus() ==
                            PreAlertStatus.ACTIVE;


            // =====================================================
            // V1 JAVA SENSOR RULE EVALUATION
            // =====================================================

            RuleEvaluationResult ruleResult =
                    aiSensorRulesService.evaluate(
                            packet.getHeartRate(),
                            (int) Math.round(
                                    packet.getMovement()
                            ),
                            packet.getDeviceWorn(),
                            packet.getBluetoothConnected(),
                            preAlertActive
                    );


            // =====================================================
            // OFF-BODY ANALYSIS
            // =====================================================

            OffBodyAnalysisResult offBodyResult =
                    offBodyIntelligenceService.analyze(
                            packet.getDeviceId(),
                            packet.getDeviceWorn(),
                            packet.getHeartRate(),
                            (int) Math.round(
                                    packet.getMovement()
                            ),
                            packet.getBluetoothConnected()
                    );


            // =====================================================
            // GPS ANALYSIS
            // =====================================================

            GpsAnalysisResult gpsResult =
                    gpsIntelligenceService.analyze(
                            packet.getDeviceId()
                    );


            // =====================================================
            // V1 AI DISABLED
            // =====================================================
            //
            // The external Python AI prediction path is V2.
            //
            // Therefore:
            //
            // prediction = null
            // decision   = MONITOR
            //
            // The existing SensorContext remains compatible with
            // the existing sensor-fusion pipeline.
            // =====================================================

            PredictionResponse prediction = null;

            ProtectionDecision decision =
                    ProtectionDecision.MONITOR;

            log.debug(
                    "V1_AI_DISABLED | deviceId={} | using Java sensor rules and fusion",
                    packet.getDeviceId()
            );


            // =====================================================
            // SENSOR CONTEXT
            // =====================================================

            SensorContext context =
                    SensorContext.builder()
                            .preAlertActive(
                                    preAlertActive
                            )
                            .offBodyResult(
                                    offBodyResult
                            )
                            .gpsResult(
                                    gpsResult
                            )
                            .prediction(
                                    prediction
                            )
                            .aiDecision(
                                    decision
                            )
                            .build();


            // =====================================================
            // V1 JAVA RULE -> INCIDENT
            // =====================================================

            if (Boolean.TRUE.equals(
                    ruleResult.getTriggerSos()
            )) {

                log.error(
                        "AI_RULE_TRIGGERED | type={} | risk={}",
                        ruleResult.getRuleType(),
                        ruleResult.getRiskScore()
                );

                incidentResponseService.processIncident(
                        session,
                        ruleResult.getRuleType().name(),
                        ruleResult.getRiskScore(),
                        true
                );
            }


            // =====================================================
            // SENSOR FUSION
            // =====================================================

            sensorFusionOrchestratorService.processFusion(
                    session,
                    context
            );


            // =====================================================
            // LOCATION HISTORY
            // =====================================================

            LocationHistory history =
                    LocationHistory.builder()
                            .deviceId(
                                    packet.getDeviceId()
                            )
                            .userId(
                                    session.getUserId()
                            )
                            .latitude(
                                    BigDecimal.valueOf(
                                            packet.getLatitude()
                                    )
                            )
                            .longitude(
                                    BigDecimal.valueOf(
                                            packet.getLongitude()
                                    )
                            )
                            .capturedAt(
                                    LocalDateTime.now()
                            )
                            .build();

            locationHistoryRepository.save(
                    history
            );

        } else {

            log.warn(
                    "SESSION_NOT_FOUND | deviceId={}",
                    packet.getDeviceId()
            );
        }


        // =========================================================
        // REGISTERED DEVICE HEARTBEAT
        // =========================================================

        RegisteredDevice device =
                repository
                        .findById(
                                packet.getDeviceId()
                        )
                        .orElseGet(
                                RegisteredDevice::new
                        );

        device.setDeviceId(
                packet.getDeviceId()
        );

        device.setFirmwareVersion(
                packet.getFirmwareVersion()
        );

        device.setBatteryLevel(
                packet.getBatteryLevel()
        );

        device.setLastHeartbeatAt(
                LocalDateTime.now()
        );

        device.setActive(
                true
        );

        repository.save(
                device
        );

        log.info(
                "DEVICE_HEARTBEAT_SAVED | deviceId={}",
                packet.getDeviceId()
        );
    }
}