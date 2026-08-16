package com.womensafety.authservice.controller;

import com.womensafety.authservice.advice.ResponseWrapper;
import com.womensafety.authservice.dto.AuthRequest;
import com.womensafety.authservice.dto.AuthResponse;
import com.womensafety.authservice.dto.RegisterRequest;
import com.womensafety.authservice.exception.InvalidCredentialsException;
import com.womensafety.authservice.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;
    private WebRequest mockRequest;

    @BeforeEach
    void setup() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
        mockRequest = mock(WebRequest.class);
    }

    @Test
    void testRegisterUserDirectly() {
        // Given
        RegisterRequest request = new RegisterRequest("john", "john@example.com","9876543210", "pass123");
        AuthResponse expected = new AuthResponse("jwt-token", "User registered successfully", UUID.randomUUID(),UUID.randomUUID());

        when(authService.register(request)).thenReturn(expected);

        // When
        ResponseEntity<ResponseWrapper<AuthResponse>> response = authController.registerUser(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getData().getToken());
        assertEquals("User registered successfully", response.getBody().getData().getMessage());
    }

    @Test
    void testLoginUserSuccess() {
        // Given
        AuthRequest request = new AuthRequest("john@gmail.com","pass123");
        AuthResponse expected = new AuthResponse("jwt-login-token", "Login successfully",UUID.randomUUID(),null);

        when(authService.login(any(AuthRequest.class))).thenReturn(expected);

        // When
        ResponseEntity<ResponseWrapper<AuthResponse>> response = authController.loginUser(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-login-token", response.getBody().getData().getToken());
        assertEquals("Login successfully", response.getBody().getData().getMessage());
    }

    @Test
    void testHandleInvalidCredentials() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Wrong username or password");

        ResponseEntity<ResponseWrapper<Object>> response = authController.handleInvalidCredentialsException(ex, mockRequest);

        assertEquals(401, response.getStatusCodeValue());

        ResponseWrapper<?> body = (ResponseWrapper<?>) response.getBody();
        assertNotNull(body);
        assertEquals("Wrong username or password", body.getMessage());
        assertEquals("ERROR", body.getStatus());
    }
}
