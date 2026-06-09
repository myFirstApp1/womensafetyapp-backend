package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.RuleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuleEvaluationResult {

    private RuleType ruleType;

    private Integer riskScore;

    private Boolean triggerSos;

    private String reason;
}