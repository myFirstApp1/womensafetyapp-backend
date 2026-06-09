package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.GpsStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GpsAnalysisResult {

    private GpsStatus status;

    private Integer riskScore;

    private String reason;
}