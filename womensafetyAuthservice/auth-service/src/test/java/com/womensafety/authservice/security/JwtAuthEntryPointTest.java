package com.womensafety.authservice.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtAuthEntryPointTest {

    private JwtAuthEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new JwtAuthEntryPoint();
    }

    @Test
    void testCommence_ShouldSendUnauthorizedError() throws IOException, ServletException {
        // Mock HTTP request, response and auth exception
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(authException.getMessage()).thenReturn("Token expired");

        // Call commence
        entryPoint.commence(request, response, authException);

        // Verify 401 is sent with correct message
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token expired");
    }
}
