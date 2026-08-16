package com.womensafety.sosservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VitalsUpdateRequest(

        @NotNull
        UUID userId,

        @NotNull
        Integer heartRate,

        @NotNull
        Integer hrv,

        @NotNull
        Double movement,

        @NotNull
        Double speed,

        @NotNull
        Double accelX,

        @NotNull
        Double accelY,

        @NotNull
        Double accelZ,

        @NotNull
        Double gyroX,

        @NotNull
        Double gyroY,

        @NotNull
        Double gyroZ,

        @NotNull
        Integer battery,

        @NotNull
        Integer worn

) {
}