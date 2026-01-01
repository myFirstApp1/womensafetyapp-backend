package com.tl.womensafety.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordResetEvent {

    private UUID eventId;
    private UUID userId;
    private String email;
    private String token;
    private Instant createdAt;
}
