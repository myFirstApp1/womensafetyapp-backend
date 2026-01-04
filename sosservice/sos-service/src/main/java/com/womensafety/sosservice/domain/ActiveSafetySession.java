package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "active_safety_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSafetySession {

    @Id
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    private String deviceId;

    private LocalDateTime lastPingTime;

    private boolean isProtected;

    private Integer batteryLevel;

    @Column(name = "last_latitude", precision = 10, scale = 6)
    private BigDecimal lastLatitude;

    @Column(name = "last_longitude", precision = 10, scale = 6)
    private BigDecimal lastLongitude;

    private boolean emergencyTriggered;

    private boolean emergencyContactNotified;

    private LocalDateTime sessionStartTime;
}