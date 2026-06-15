package com.womensafety.authservice.service;

import com.tl.womensafety.common.events.PasswordResetEvent;
import com.tl.womensafety.common.events.UserCreatedEvent;
import com.womensafety.authservice.dto.RegisterRequest;
import com.womensafety.authservice.exception.EmailAlreadyExistsException;
import com.womensafety.authservice.exception.InvalidCredentialsException;
import com.womensafety.authservice.exception.InvalidResetTokenException;
import com.womensafety.authservice.model.PasswordResetToken;
import com.womensafety.authservice.model.VerificationToken;
import com.womensafety.authservice.otp.OtpChannel;
import com.womensafety.authservice.otp.OtpCreateRequest;
import com.womensafety.authservice.otp.OtpService;
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
    OtpService otpService;
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
    @Value("${app.user.created.topic}")
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
        // Temporarily mark as verified (until SMTP ready)
        user.setIsVerified(false);
        User saved = userRepository.save(user);
        log.info("User '{}' registered successfully", request.getUsername());

        // CREATE OTP
        OtpCreateRequest otpReq = new OtpCreateRequest(
                saved.getId(),
                OtpChannel.EMAIL,
                saved.getEmail()
        );
        OtpService.CreateOtpResult otp = otpService.createOtp(otpReq);
        UserCreatedEvent event = new UserCreatedEvent(
                UUID.randomUUID(),        // eventId
                saved.getId(),            // userId (UUID)
                saved.getEmail(),
                null,         // optional if present in entity
                saved.getIsVerified(),
                Instant.now(),
                saved.getUsername()
        );
        publishEvent(topicName, event);
        log.info("Published user.created event for userId {}", saved.getId());

       // String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(null, "OTP sent to your email", user.getId(),otp.getTxnId());
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->{
                    log.warn("Login failed: email '{}' not found", request.getEmail());
                    return new UsernameNotFoundException("Email not found");
                });
        if (!user.getIsVerified()) {
            throw new InvalidCredentialsException("Please verify OTP before login");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid credentials for email '{}'", request.getEmail());
            throw new InvalidCredentialsException("Invalid email  or password");
        }
        log.info("Login successful for email: {}", request.getEmail());
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "Login successful", user.getId(),null);
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
        publishEvent(emailTopic, event);
    }

    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new InvalidResetTokenException("Password reset token is invalid or expired"));

        // Check if already used
        if (resetToken.isUsed()) {
            throw new InvalidResetTokenException("Password reset token already used");
        }

        // Check expiry
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Password reset token is invalid or expired");
        }

        // Update password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getEmail());
    }

    private void publishEvent(String topic, Object event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish Kafka event to topic {} : {}", topic, ex.getMessage(), ex);

                        // IMPORTANT
                        // In production, persist to OUTBOX table here
                        // For now, logging is enough
                    } else {
                        log.info("Kafka event published to topic {}", topic);
                    }
                });
    }

}

