package com.womensafety.authservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegisterRequestTest {
    RegisterRequest authReq = new RegisterRequest();
    @Test
    void testAuthRequest() {
              // When
        authReq.setUsername("john");
        authReq.setEmail("john@example.com");
        authReq.setPassword("password123");

        // Then
        assertEquals("john", authReq.getUsername());
        assertEquals("john@example.com", authReq.getEmail());
        assertEquals("password123", authReq.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        RegisterRequest authReq = new RegisterRequest("alice", "alice@example.com", "secret");
        assertEquals("alice", authReq.getUsername());
        assertEquals("alice@example.com", authReq.getEmail());
        assertEquals("secret", authReq.getPassword());
    }
}

