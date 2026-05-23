package com.womensafety.sosservice.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class LocationRecoveryEvent {

    private UUID userId;

    private String deviceId;

    private Double latitude;

    private Double longitude;

    private LocalDateTime lastSeenAt;

    private String reason;
}