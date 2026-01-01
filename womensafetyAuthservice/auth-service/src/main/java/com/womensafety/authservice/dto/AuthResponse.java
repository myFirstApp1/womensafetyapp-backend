package com.womensafety.authservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
    private UUID userId;

    public AuthResponse(String token, String message,UUID userId) {
        this.token = token;
        this.message = message;
        this.userId = userId;
    }
}
