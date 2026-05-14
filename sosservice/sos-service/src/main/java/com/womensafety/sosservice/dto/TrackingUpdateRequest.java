package com.womensafety.sosservice.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;
public record TrackingUpdateRequest(

        @NotNull
        UUID userId,

        @NotBlank
        String trackingId,

        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        double latitude,

        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        double longitude,

        @PositiveOrZero
        double accuracyMeters,

        @PositiveOrZero
        double speed
) {}