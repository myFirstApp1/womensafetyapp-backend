package com.womensafety.sosservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorContext {

    private Integer heartRate;

    private Integer movementScore;

    private boolean deviceWorn;

    private boolean bluetoothConnected;

    private HeartbeatLossResult heartbeatLoss;

    private TamperAnalysisResult tamperResult;

    private OffBodyAnalysisResult offBodyResult;

    private GpsAnalysisResult gpsResult;

    private boolean preAlertActive;
}