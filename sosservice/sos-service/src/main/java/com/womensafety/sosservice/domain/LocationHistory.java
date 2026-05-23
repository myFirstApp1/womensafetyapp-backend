package com.womensafety.sosservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "location_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID userId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime capturedAt;
}