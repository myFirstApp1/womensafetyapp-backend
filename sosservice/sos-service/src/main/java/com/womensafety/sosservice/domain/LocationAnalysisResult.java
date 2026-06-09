package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.LocationEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationAnalysisResult {

    private LocationEventType eventType;

    private int riskScore;

    private boolean emergency;
}