package com.womensafety.authservice.dto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthRequestTest {
    @Test
    void testAuthRequest() {
        // Given
        AuthRequest authReq = new AuthRequest();

        // When
        authReq.setUsername("john");
        authReq.setPassword("password123");

        // Then
        assertEquals("john", authReq.getUsername());
        assertEquals("password123", authReq.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        AuthRequest authReq = new AuthRequest("alice", "secret");
        assertEquals("alice", authReq.getUsername());
        assertEquals("secret", authReq.getPassword());
    }
}


