package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "evidence_records",
        indexes = {
                @Index(
                        name = "idx_tracking_id",
                        columnList = "tracking_id"
                ),
                @Index(
                        name = "idx_file_type",
                        columnList = "file_type"
                ),
                @Index(
                        name = "idx_uploaded_at",
                        columnList = "uploaded_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String evidenceId;
    @Column(name = "tracking_id")
    private String trackingId;
    @Column(name = "file_type")
    @Enumerated(EnumType.STRING)
    private EvidenceType fileType;
    @Column(
            nullable = false,
            length = 1000
    )
    private String storageUrl;

    @Column(length = 255)
    private String hashValue;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @Column(length = 100)
    private String uploadedBy;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (uploadedAt == null) {
            uploadedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();
    }
}