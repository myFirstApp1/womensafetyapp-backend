package com.womensafety.sosservice.scheduler;

import com.womensafety.sosservice.domain.RegisteredDevice;
import com.womensafety.sosservice.repository.RegisteredDeviceRepository;

import com.womensafety.sosservice.service.heartbeat.HeartbeatLossService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeartbeatMonitorJob {
    private final RegisteredDeviceRepository
            registeredDeviceRepository;
    private final HeartbeatLossService
            heartbeatLossService;

    @Scheduled(fixedDelay = 60000)
    public void monitorHeartbeats() {

        LocalDateTime threshold =
                LocalDateTime.now()
                        .minusMinutes(5);

        List<RegisteredDevice> staleDevices =
                registeredDeviceRepository
                        .findByLastHeartbeatAtBefore(
                                threshold
                        );

        if (staleDevices.isEmpty()) {

            log.info(
                    "HEARTBEAT_MONITOR | No stale devices"
            );

            return;
        }

        for (RegisteredDevice device : staleDevices)  {

            if (Boolean.TRUE.equals(device.getActive())) {

                device.setActive(false);

                registeredDeviceRepository.save(device);

                // NEW
                heartbeatLossService.handleHeartbeatLoss(
                        device.getDeviceId()
                );

                log.error("""
                DEVICE_OFFLINE
                deviceId={}
                lastHeartbeat={}
                """,
                        device.getDeviceId(),
                        device.getLastHeartbeatAt()
                );
            }
        }
    }
}
