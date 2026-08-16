package com.womensafety.sosservice.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_predictions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    private String requestId;

    private String prediction;

    private Double confidence;

    private Integer riskScore;

    private String riskLevel;

    private String dangerLevel;
    private String recommendedAction;
    private String modelVersion;
    private LocalDateTime createdAt;

}