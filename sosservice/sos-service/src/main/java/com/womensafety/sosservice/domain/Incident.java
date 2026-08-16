package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.IncidentStatus;
import com.womensafety.sosservice.domain.enums.IncidentTriggerType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tracking_id")
    private String trackingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private IncidentTriggerType triggerType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "warning_at")
    private LocalDateTime warningAt;

    @Column(name = "danger_at")
    private LocalDateTime dangerAt;

    @Column(name = "tracking_started_at")
    private LocalDateTime trackingStartedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "incident_source")
    private String incidentSource;
}
