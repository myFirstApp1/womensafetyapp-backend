package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.IncidentEventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentEvent {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "tracking_id")
    private String trackingId;

    @Enumerated(EnumType.STRING)
    private IncidentEventType eventType;

    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
