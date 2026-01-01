package com.womensafety.authservice.service;

import com.tl.womensafety.common.events.EmailVerificationEvent;
import com.tl.womensafety.common.events.PasswordResetEvent;
import com.tl.womensafety.common.events.UserCreatedEvent;
import com.womensafety.authservice.dto.RegisterRequest;
import com.womensafety.authservice.exception.EmailAlreadyExistsException;
import com.womensafety.authservice.exception.InvalidCredentialsException;
import com.womensafety.authservice.model.PasswordResetToken;
import com.womensafety.authservice.model.VerificationToken;
import com.womensafety.authservice.repository.PasswordResetTokenRepository;
import com.womensafety.authservice.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.womensafety.authservice.dto.AuthRequest;
import com.womensafety.authservice.dto.AuthResponse;
import com.womensafety.authservice.model.User;
import com.womensafety.authservice.repository.UserRepository;
import com.womensafety.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordResetTokenRepository tokenRepository;
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${app.verification.topic}")
    private String topicName;
    @Value("${app.email.password-reset.topic:send.email.password-reset}")
    private String emailTopic;
    @Value("${app.email.topic}")
    private String emailTopicName;

    public AuthResponse register(RegisterRequest request) {
        log.info("Validating registration for user: {}", request.getUsername());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email '" + request.getEmail() + "' is already registered");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");
        // ✅ Temporarily mark as verified (until SMTP ready)
        user.setIsVerified(true);
        User saved = userRepository.save(user);
        log.info("User '{}' registered successfully", request.getUsername());

        // Generate token
        String tokenEmail = UUID.randomUUID().toString();
        createVerificationToken(saved, tokenEmail);
        log.info("TOPIC = {}", emailTopicName);
// TODO: Enable email verification once production SMTP is configured

// Publish event for Notification Service
        log.info("Publishing email verification event for {} with token {}", saved.getEmail(), tokenEmail);
        kafkaTemplate.send(emailTopicName, new EmailVerificationEvent(
                UUID.randomUUID(),        // eventId
                saved.getId(),            // userId (UUID)
                saved.getEmail(),
                tokenEmail,
                Instant.now() ,       //  occurredAt\
                Instant.now()
        ));
        UserCreatedEvent event = new UserCreatedEvent(
                UUID.randomUUID(),        // eventId
                saved.getId(),            // userId (UUID)
                saved.getEmail(),
                null,         // optional if present in entity
                saved.getIsVerified(),
                Instant.now(),
                saved.getUsername()
        );
        kafkaTemplate.send(topicName, event);
        log.info("Published user.created event for userId {}", saved.getId());

        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "User registered successfully", user.getId());
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->{
                    log.warn("Login failed: user '{}' not found", request.getUsername());
                    return new UsernameNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for user '{}'", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }
        log.info("Login successful for user: {}", request.getUsername());
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "Login successful", user.getId());
    }


    @Transactional
    public void verifyUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
        userRepository.verifyUser(userId);
    }

    @Transactional
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24)) // 24 hours validity
                .build();
        verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void verifyUserByToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token expired");
        }

        User user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken); // one-time use
    }
    public void forgotPassword(String email) {
        log.info("Forgot password requested for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // IMPORTANT: do nothing, return silently
           return;
        }
        User user = userOpt.get();
        // 1. Generate token
        String token = UUID.randomUUID().toString();
        // 2. Save token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();
        tokenRepository.save(resetToken);
        // 3. Publish Kafka event
        PasswordResetEvent event = new PasswordResetEvent(
                UUID.randomUUID(),
                user.getId(),
                user.getEmail(),
                token,
                Instant.now()
        );
        log.info("Publishing PasswordResetEvent to topic {}", emailTopic);
        kafkaTemplate.send(emailTopic, event);
    }
}

