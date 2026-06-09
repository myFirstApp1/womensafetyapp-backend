package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.TamperEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TamperAnalysisResult {

    private TamperEventType tamperType;

    private int riskScore;

    private boolean emergency;
}