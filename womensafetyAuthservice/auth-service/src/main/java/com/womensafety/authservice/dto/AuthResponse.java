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
    private UUID txnId;

    public AuthResponse(String token, String message,UUID userId,UUID txnId) {
        this.token = token;
        this.message = message;
        this.userId = userId;
        this.txnId=txnId;
    }
}
