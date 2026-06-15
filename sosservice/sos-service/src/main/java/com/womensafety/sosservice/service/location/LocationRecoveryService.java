package com.womensafety.sosservice.service.location;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.domain.LocationRecoveryEvent;
import com.womensafety.sosservice.repository.ActiveSafetySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationRecoveryService {

    private final ActiveSafetySessionRepository
            activeSafetySessionRepository;

    public void capture(
            String deviceId,
            String reason
    ) {

        ActiveSafetySession session =
                activeSafetySessionRepository
                        .findByDeviceId(deviceId)
                        .orElse(null);

        if (session == null) {

            log.warn(
                    "LOCATION_RECOVERY_FAILED | deviceId={}",
                    deviceId
            );

            return;
        }

        LocationRecoveryEvent event =
                LocationRecoveryEvent.builder()
                        .userId(
                                session.getUserId()
                        )
                        .deviceId(
                                session.getDeviceId()
                        )
                        .latitude(
                                session.getLastLatitude() == null
                                        ? null
                                        : session.getLastLatitude()
                                        .doubleValue()
                        )
                        .longitude(
                                session.getLastLongitude() == null
                                        ? null
                                        : session.getLastLongitude()
                                        .doubleValue()
                        )
                        .lastSeenAt(
                                session.getLastPingTime()
                        )
                        .reason(
                                reason
                        )
                        .build();

        log.error("""
                LOCATION_RECOVERY_EVENT
                userId={}
                deviceId={}
                latitude={}
                longitude={}
                lastSeenAt={}
                reason={}
                """,
                event.getUserId(),
                event.getDeviceId(),
                event.getLatitude(),
                event.getLongitude(),
                event.getLastSeenAt(),
                event.getReason()
        );
    }
}