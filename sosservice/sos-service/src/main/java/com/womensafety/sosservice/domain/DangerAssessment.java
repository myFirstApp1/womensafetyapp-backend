package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.DangerLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DangerAssessment {

    private Integer riskScore;

    private DangerLevel dangerLevel;

    private Boolean autoSos;

    private String reason;
}