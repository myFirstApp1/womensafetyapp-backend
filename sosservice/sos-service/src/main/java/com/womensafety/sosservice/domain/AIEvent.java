package com.womensafety.sosservice.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AIEvent {

    private UUID userId;

    private String prediction;

    private String dangerLevel;

    private Integer riskScore;

    private String reason;

    private LocalDateTime timestamp;

}