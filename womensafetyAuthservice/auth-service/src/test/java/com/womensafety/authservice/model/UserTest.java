package com.womensafety.authservice.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    @Test
    void testUserPojo() {
        // Given
        User user = new User();

        // When
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("password123");

        // Then
        assertEquals("john", user.getUsername());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(UUID.randomUUID(),"alice", "alice@example.com", "secret","USER",false);
        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals("USER", user.getRole());
    }
}


