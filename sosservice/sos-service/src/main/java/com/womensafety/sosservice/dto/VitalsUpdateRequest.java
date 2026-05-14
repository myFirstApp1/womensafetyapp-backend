package com.womensafety.sosservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VitalsUpdateRequest(

        @NotNull
        UUID userId,

        @Min(20)
        @Max(250)
        int heartRate,

        @Min(0)
        @Max(100)
        int movementScore
) {}
