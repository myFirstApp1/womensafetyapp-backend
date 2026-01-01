package com.tl.womensafety.common.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Emitted when an email verification is initiated.
 * Typically produced by auth-service and consumed by notification-service.
 */
public record EmailVerificationEvent(
        UUID eventId,
        UUID userId,
        String email,
        String token,
        Instant expiresAt,
        Instant occurredAt
) {}
