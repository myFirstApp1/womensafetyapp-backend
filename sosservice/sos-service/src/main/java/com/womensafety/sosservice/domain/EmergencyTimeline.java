package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "emergency_timeline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trackingId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    private String eventType;

    @Column(length = 1000)
    private String eventData;

    private LocalDateTime createdAt;
}