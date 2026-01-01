package com.womensafety.authservice.controller;

import com.womensafety.authservice.advice.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/secure")
public class SecureTestController {
    @GetMapping("/test")
    public ResponseEntity<ResponseWrapper<String>> secureTest() {
        log.info("Secure endpoint accessed");
        return ResponseEntity.ok(ResponseWrapper.success("JWT verified! Access granted.", "Welcome"));
    }
}
