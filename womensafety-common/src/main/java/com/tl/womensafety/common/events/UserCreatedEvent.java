package com.tl.womensafety.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted after a user is successfully created in auth-service.
 * Consumed by user-service to initialize profile, etc.
 */
public record UserCreatedEvent(
        UUID eventId,
        UUID userId,
        String email,
        String phone,
        boolean isVerified,
        Instant occurredAt,
        String userName
) {}