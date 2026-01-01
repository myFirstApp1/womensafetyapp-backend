package com.tl.womensafety.common.dto;

import java.time.Instant;

/**
 * Basic location snapshot (e.g., for SOS or timeline events).
 */
public record Location(
        double latitude,
        double longitude,
        Double accuracyMeters, // nullable if unknown
        Instant timestamp
) {}
