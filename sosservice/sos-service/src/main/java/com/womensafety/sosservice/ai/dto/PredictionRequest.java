package com.womensafety.sosservice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {

    private Integer heartRate;

    private Integer hrv;

    private Double movement;

    private Double speed;

    private Double accelX;

    private Double accelY;

    private Double accelZ;

    private Double gyroX;

    private Double gyroY;

    private Double gyroZ;

    private Integer worn;

    private Integer battery;

}