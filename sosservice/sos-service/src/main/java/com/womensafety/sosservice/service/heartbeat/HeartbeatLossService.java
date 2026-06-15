package com.womensafety.sosservice.service.heartbeat;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.HeartbeatLossResult;
import com.womensafety.sosservice.domain.enums.HeartbeatLossType;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import com.womensafety.sosservice.service.incident.IncidentResponseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeartbeatLossService {

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;
    private final IncidentResponseService
            incidentResponseService;

    public HeartbeatLossResult analyze(
            Integer batteryLevel,
            boolean deviceWorn,
            boolean bluetoothConnected
    ) {

        if (batteryLevel != null
                && batteryLevel <= 2) {

            return new HeartbeatLossResult(
                    HeartbeatLossType.BATTERY_DEAD,
                    10,
                    false
            );
        }

        if (!deviceWorn
                && !bluetoothConnected) {

            return new HeartbeatLossResult(
                    HeartbeatLossType.POSSIBLE_ATTACK,
                    100,
                    true
            );
        }

        if (!bluetoothConnected) {

            return new HeartbeatLossResult(
                    HeartbeatLossType.SIGNAL_LOST,
                    40,
                    false
            );
        }

        return new HeartbeatLossResult(
                HeartbeatLossType.DEVICE_OFFLINE,
                50,
                false
        );
    }

    @Transactional
    public void handleHeartbeatLoss(
            String deviceId
    ) {

        ActiveSafetySession session =
                activeSafetySessionRepository
                        .findByDeviceId(deviceId)
                        .orElse(null);

        if (session == null) {

            log.warn(
                    "NO_ACTIVE_SESSION | deviceId={}",
                    deviceId
            );

            return;
        }

        incidentResponseService.processIncident(
                session,
                "HEARTBEAT_LOSS",
                40,
                false
        );

        log.error(
                "HEARTBEAT_LOSS_DETECTED | userId={}",
                session.getUserId()
        );
    }
}