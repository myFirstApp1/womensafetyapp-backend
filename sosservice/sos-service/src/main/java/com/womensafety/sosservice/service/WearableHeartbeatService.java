package com.womensafety.sosservice.service;

import com.womensafety.sosservice.domain.*;
import com.womensafety.sosservice.domain.enums.PreAlertStatus;
import com.womensafety.sosservice.dto.HeartbeatPacket;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.repository.LocationHistoryRepository;
import com.womensafety.sosservice.repository.RegisteredDeviceRepository;
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
                    packet.getMovementScore()
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
            boolean preAlertActive =
                    session.getPreAlertStatus() ==
                            PreAlertStatus.ACTIVE;

            RuleEvaluationResult ruleResult =
                    aiSensorRulesService.evaluate(
                            packet.getHeartRate(),
                            packet.getMovementScore(),
                            packet.getDeviceWorn(),
                            packet.getBluetoothConnected(),
                            preAlertActive
                    );
            OffBodyAnalysisResult offBodyResult =
                    offBodyIntelligenceService.analyze(
                            packet.getDeviceId(),
                            packet.getDeviceWorn(),
                            packet.getHeartRate(),
                            packet.getMovementScore(),
                            packet.getBluetoothConnected()
                    );

            GpsAnalysisResult gpsResult =
                    gpsIntelligenceService.analyze(
                            packet.getDeviceId()
                    );
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
                            .build();

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
            sensorFusionOrchestratorService.processFusion(
                    session,
                    context
            );
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
        }else {

            log.warn(
                    "SESSION_NOT_FOUND | deviceId={}",
                    packet.getDeviceId()
            );
        }
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

        device.setActive(true);

        repository.save(device);

        log.info(
                "DEVICE_HEARTBEAT_SAVED | deviceId={}",
                packet.getDeviceId()
        );
    }
}