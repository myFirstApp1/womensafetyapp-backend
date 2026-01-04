package com.womensafety.authservice.outbox;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_unpublished", columnList = "published_at"),
                @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type, aggregate_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_outbox_event_id", columnNames = {"event_id"})
        }
)
public class OutboxEvent {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "event_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID eventId;

    @Column(name = "aggregate_type", nullable = false, length = 64)
    private String aggregateType; // e.g., "USER"

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 64)
    private String eventType;     // e.g., "USER_VERIFIED"

    // Use TEXT if you prefer; JSON type is supported on MySQL 8+
    @Lob
    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload; // serialized JSON

    @Lob
    @Column(name = "headers_json", columnDefinition = "JSON")
    private String headersJson; // serialized JSON of headers/metadata

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;
    @Column(name = "topic", nullable = false)
    private String topic;

    public boolean isPublished() {
        return publishedAt != null;
    }
}
