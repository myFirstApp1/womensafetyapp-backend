package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sos_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SosOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OutboxStatus status;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "event_id", unique = true)
    private String eventId;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "tracking_id")
    private String trackingId;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}