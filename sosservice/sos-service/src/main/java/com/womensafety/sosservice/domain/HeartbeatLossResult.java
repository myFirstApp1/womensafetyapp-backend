package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.HeartbeatLossType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HeartbeatLossResult {

    private HeartbeatLossType type;

    private int riskScore;

    private boolean emergency;
}