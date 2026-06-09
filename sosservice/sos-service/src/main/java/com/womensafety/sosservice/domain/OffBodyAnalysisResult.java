package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.OffBodyEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OffBodyAnalysisResult {

    private OffBodyEventType eventType;
    private int riskScore;
    private boolean dangerous;
}