package com.womensafety.authservice.controller;

import com.womensafety.authservice.advice.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

 class SecureTestControllerTest {
   private SecureTestController secureTestController;

    @BeforeEach
    void setup() {
        secureTestController = new SecureTestController();
    }

    @Test
    void testSecureEndpointReturnsExpectedMessage() {
        ResponseEntity<ResponseWrapper<String>> result = secureTestController.secureTest();
        assertEquals("JWT verified! Access granted.", result.getBody().getMessage());
    }
}