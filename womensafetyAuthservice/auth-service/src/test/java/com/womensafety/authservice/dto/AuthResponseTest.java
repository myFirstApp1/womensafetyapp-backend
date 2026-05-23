package com.womensafety.authservice.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthResponseTest {

    AuthResponse response = new AuthResponse();
    @Test
    void testAuthRequest() {
        // When
        response.setMessage("Login Successfully");
        response.setToken("Jwt Token");

        // Then
        assertEquals("Login Successfully", response.getMessage());
        assertEquals("Jwt Token", response.getToken());
    }

    @Test
    void testAllArgsConstructor() {
        response = new AuthResponse("Jwt Token","Login Successfully", UUID.randomUUID(),UUID.randomUUID());
        assertEquals("Login Successfully", response.getMessage());
        assertEquals("Jwt Token", response.getToken());
    }
}
