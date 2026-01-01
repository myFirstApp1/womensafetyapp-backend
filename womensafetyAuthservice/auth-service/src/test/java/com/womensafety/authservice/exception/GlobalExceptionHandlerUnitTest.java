package com.womensafety.authservice.exception;

import com.womensafety.authservice.advice.ResponseWrapper;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerUnitTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void testHandleClientError() {
        HttpClientErrorException ex = mock(HttpClientErrorException.class);
        when(ex.getMessage()).thenReturn("Bad Request");
        ResponseEntity<ResponseWrapper<Object>> response = exceptionHandler.handleHttpClientError(ex, webRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testHandleServerError() {
        HttpServerErrorException ex = mock(HttpServerErrorException.class);
        when(ex.getMessage()).thenReturn("Internal Error");
        ResponseEntity<ResponseWrapper<Object>> response = exceptionHandler.handleHttpServerError(ex, webRequest);
        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testHandleConstraintViolation() {
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", null);
        ResponseEntity<ResponseWrapper<Object>> response = exceptionHandler.handleConstraintViolation(ex, webRequest);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void testHandleSocketTimeout() {
        SocketTimeoutException ex = new SocketTimeoutException("timeout");
        ResponseEntity<ResponseWrapper<Object>> response = exceptionHandler.handleSocketTimeout(ex, webRequest);
        assertEquals(504, response.getStatusCodeValue());
    }

    @Test
    void testHandleGenericException() {
        Exception ex = new Exception("Generic problem");
        ResponseEntity<ResponseWrapper<Object>> response = exceptionHandler.handleGenericException(ex, webRequest);
        assertEquals(500, response.getStatusCodeValue());
    }
}