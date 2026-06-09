package com.womensafety.authservice.dto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthRequestTest {
    @Test
    void testAuthRequest() {
        // Given
        AuthRequest authReq = new AuthRequest();

        // When
        authReq.setEmail("john@gmail.com");
        authReq.setPassword("password123");

        // Then
        assertEquals("john@gmail.com", authReq.getEmail());
        assertEquals("password123", authReq.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        AuthRequest authReq = new AuthRequest("alice@gmail.com", "secret");
        assertEquals("alice@gmail.com", authReq.getEmail());
        assertEquals("secret", authReq.getPassword());
    }
}


