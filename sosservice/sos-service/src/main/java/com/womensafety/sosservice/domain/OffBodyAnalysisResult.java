package com.womensafety.sosservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OffBodyAnalysisResult {

    private OffBodyEventType eventType;
    private int riskScore;
    private boolean dangerous;
}