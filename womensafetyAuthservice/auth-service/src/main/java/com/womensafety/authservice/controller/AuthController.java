package com.womensafety.authservice.controller;

import com.womensafety.authservice.advice.ResponseWrapper;
import com.womensafety.authservice.common.OnCreateGroupValidator;
import com.womensafety.authservice.dto.*;
import com.womensafety.authservice.exception.InvalidCredentialsException;
import com.womensafety.authservice.service.AuthService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostConstruct
    public void init() {
        log.info("LOGGING from @PostConstruct - auth-service started");
    }

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<AuthResponse>> registerUser(@Validated(OnCreateGroupValidator.class) @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register called for user: {}", request.getUsername());
        return ResponseEntity.ok(ResponseWrapper.success("User Register successful", authService.register(request)));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseWrapper<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok(
                ResponseWrapper.success(
                        "If the email exists, a password reset link has been sent",
                        null
                )
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(
                ResponseWrapper.success(
                        "Password reset successful",
                        null
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthResponse>> loginUser(@Validated(OnCreateGroupValidator.class)
                                                                       @RequestBody AuthRequest request) {
        log.info("POST /api/auth/login called for user: {}", request.getEmail());
        return ResponseEntity.ok(ResponseWrapper.success("Login successful", authService.login(request)));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request){
        log.error("Invalid credentials: ", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(ex.getMessage()));
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<ResponseWrapper<String>> verifyUser(@PathVariable UUID id) {
        authService.verifyUser(id);
        return ResponseEntity.ok(ResponseWrapper.success("User verified successfully",null));
    }

    @GetMapping("/verify")
    public ResponseEntity<ResponseWrapper<String>> verifyEmail(@RequestParam String token) {
        authService.verifyUserByToken(token);
        return ResponseEntity.ok(ResponseWrapper.success("User verified successfully",null));
    }
    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(Authentication authentication) {

        authService.logout(authentication.getName());

        return ResponseEntity.ok(
                ResponseWrapper.success("Logout successful", null)
        );
    }

}