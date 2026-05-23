package com.womensafety.sosservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RiskDecisionResult {

    private Integer finalRiskScore;

    private boolean triggerSos;

    private String reason;
}