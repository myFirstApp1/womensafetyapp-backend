package com.womensafety.authservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String secret = "my-secret-jwt-key-for-testing1234567890"; // must be >=32 chars

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    }

    @Test
    void testGenerateAndExtractUsername() {
        String username = "john";
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);

        String extracted = jwtUtil.extractUsername(token);
        assertEquals(username, extracted);
    }

    @Test
    void testValidateToken_Success() {
        UserDetails user = User.withUsername("john").password("pass").roles("USER").build();
        String token = jwtUtil.generateToken(user.getUsername());

        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void testValidateToken_InvalidUser() {
        UserDetails user = User.withUsername("john").password("pass").roles("USER").build();
        String token = jwtUtil.generateToken("someone_else");

        assertFalse(jwtUtil.validateToken(token, user));
    }

    @Test
    void testTokenIsNotExpired() {
        String token = jwtUtil.generateToken("testuser");
        assertFalse(token.isEmpty());
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void testExtractClaims_InvalidSignature() {
        String token = jwtUtil.generateToken("john");
        String tamperedToken = token.replace("a", "b"); // force token to break

        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tamperedToken));
    }
}

