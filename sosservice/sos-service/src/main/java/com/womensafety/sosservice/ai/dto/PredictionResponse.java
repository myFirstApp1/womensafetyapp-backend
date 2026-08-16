package com.womensafety.sosservice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {

    private String requestId;

    private String prediction;

    private Double confidence;

    private Integer riskScore;

    private String riskLevel;

    private String dangerLevel;

    private String stressLevel;

    private String activity;

    private String context;

    private String recommendedAction;

    private String modelVersion;

    private List<String> reasons;

}