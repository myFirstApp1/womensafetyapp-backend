package com.womensafety.authservice.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class OtpResentEvent {

    private final UUID eventId;
    private final UUID userId;
    private final UUID otpTxnId;

    private final String channel;       // EMAIL / PHONE
    private final String destination;   // email or phone

    private final Instant occurredAt;
    private final int version;

    // 🔹 Factory method (THIS is what you asked)
    public static OtpResentEvent from(
            UUID userId,
            UUID otpTxnId,
            String channel,
            String destination
    ) {
        return OtpResentEvent.builder()
                .eventId(UUID.randomUUID())
                .userId(userId)
                .otpTxnId(otpTxnId)
                .channel(channel)
                .destination(destination)
                .occurredAt(Instant.now())
                .version(1)
                .build();
    }
}