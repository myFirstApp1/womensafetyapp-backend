package com.tl.womensafety.common.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Immutable user DTO for cross-service use.
 */
public record User(
        UUID id,
        String name,
        String email,
        String phone,
        boolean isVerified,
        String profileImageUrl,
        List<Contact> emergencyContacts,
        Instant createdAt,
        Instant updatedAt
) {}