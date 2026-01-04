package com.womensafety.authservice.otp;

import com.tl.womensafety.common.events.UserVerifiedEvent;
import com.womensafety.authservice.exception.InvalidOtpException;
import com.womensafety.authservice.exception.OtpBlockedException;
import com.womensafety.authservice.exception.OtpExpiredException;
import com.womensafety.authservice.model.User;
import com.womensafety.authservice.outbox.OutboxEvent;
import com.womensafety.authservice.outbox.OutboxEventRepository;
import com.womensafety.authservice.outbox.OutboxFactory;
import com.womensafety.authservice.outbox.OutboxPublisher;
import com.womensafety.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    @Value("${app.otp.code-length:6}")
    private int codeLength;
    @Value("${app.otp.ttl-minutes:10}")
    private int ttlMinutes;
    @Value("${app.otp.max-attempts:5}")
    private int maxAttempts;
    @Value("${app.otp.dev-return-code:true}")
    private boolean devReturnCode;

    // also inject SecureRandom via constructor (add to @RequiredArgsConstructor list)
    private final SecureRandom secureRandom;

    private final UserRepository userRepository;
    private final OtpChallengeRepository otpRepo;
    private final OutboxFactory outboxFactory;
    private final OutboxPublisher outboxPublisher;
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    public ConfirmOtpResult confirmOtp(ConfirmOtpRequest req) {
        var challenge = otpRepo.findByTxnId(req.getTxnId())
                .orElseThrow(() -> new InvalidOtpException("Invalid or unknown OTP transaction"));

        var now = Instant.now();

        if (challenge.isExpired(now)) {
            challenge.markExpired();
            otpRepo.save(challenge);
            throw new OtpExpiredException("OTP has expired");
        }
        if (challenge.isBlocked()) {
            throw new OtpBlockedException("OTP attempts exceeded or challenge blocked");
        }
        if (challenge.getStatus() == OtpStatus.VERIFIED) {
            // Idempotent success (no new event)
            return ConfirmOtpResult.success(challenge.getUserId(), challenge.getTxnId(), true);
        }

        // Verify OTP code with salt+SHA-256 (keep consistent with how you stored it)
        String expected = challenge.getCodeHash();
        String computed = sha256(challenge.getSalt() + req.getCode());
        if (!computed.equals(expected)) {
            challenge.incAttempts();
            // Optional: block if reached maxAttempts
            if (challenge.getAttempts() >= challenge.getMaxAttempts()) {
                challenge.setStatus(OtpStatus.BLOCKED);
            }
            otpRepo.save(challenge);
            throw new InvalidOtpException("OTP incorrect");
        }

        // Mark verified
        challenge.markVerified(now);
        otpRepo.save(challenge);

        User user = userRepository.findById(challenge.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!user.getIsVerified()) {
            user.setIsVerified(true);
            userRepository.save(user);
        }

        // ===== Step 7: Outbox event in SAME TXN =====
        var event = UserVerifiedEvent.from(challenge.getUserId(), challenge.getTxnId(), now);


        var headers = new HashMap<String, String>();
        var traceId = MDC.get("traceId");
        if (traceId != null) headers.put("traceId", traceId);
        headers.put("content-type", "application/json");
        headers.put("event-type", "USER_VERIFIED");
        headers.put("event-version", "1");

        OutboxEvent outbox = outboxFactory.build(
                "USER",
                challenge.getUserId(),
                "USER_VERIFIED",
                event.getEventId(),
                event,
                headers,
                "user.verified"
        );
        if (!outboxEventRepository.existsByEventId(event.getEventId())) {
            outboxEventRepository.save(outbox);
        }
        //outboxEventRepository.save(outbox);
        // ============================================

        return ConfirmOtpResult.success(challenge.getUserId(), challenge.getTxnId(), false);
    }

    @Transactional
    public ResendOtpResponse resendOtp(ResendOtpRequest req) {

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getIsVerified()) {
            throw new IllegalStateException("User already verified");
        }

        // 🔴 Expire previous OTPs for same user + channel
        otpRepo.expireAllActive(user.getId(), req.getChannel(), Instant.now());

        // 🔐 Generate new OTP
        String salt = UUID.randomUUID().toString().replace("-", "");
        String otp = generateNumericCode(codeLength);
        String hash = sha256(salt + otp);

        UUID txnId = UUID.randomUUID();

        OtpChallenge challenge = OtpChallenge.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .channel(req.getChannel())
                .destination(req.getEmail())
                .codeHash(hash)
                .salt(salt)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .attempts(0)
                .maxAttempts(5)
                .status(OtpStatus.PENDING)
                .txnId(txnId)
                .build();

        otpRepo.save(challenge);

        // DEV only (remove in prod)
        log.info("DEV ONLY OTP for txnId={} is {}", txnId, otp);

        //  Outbox event (optional but recommended)
        outboxPublisher.publishOtpEvent(user, challenge);

        return new ResendOtpResponse(user.getId(), txnId);
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    @Transactional
    public CreateOtpResult createOtp(OtpCreateRequest req) {
        // If there is an active pending challenge, you can invalidate it (optional)
        otpRepo.findTopByUserIdAndChannelOrderByIdDesc(req.getUserId(), req.getChannel())
                .filter(ch -> ch.getStatus() == OtpStatus.PENDING)
                .ifPresent(ch -> {
                    ch.markExpired();
                    otpRepo.save(ch);
                });

        String salt = UUID.randomUUID().toString().replace("-", "");
        String code = generateNumericCode(codeLength);
        String codeHash = sha256(salt + code);

        var challenge = OtpChallenge.builder()
                .userId(req.getUserId())
                .channel(req.getChannel())
                .destination(req.getDestination())
                .codeHash(codeHash)
                .salt(salt)
                .expiresAt(Instant.now().plus(ttlMinutes, ChronoUnit.MINUTES))
                .attempts(0)
                .maxAttempts(maxAttempts)
                .status(OtpStatus.PENDING)
                .txnId(UUID.randomUUID())
                .build();

        otpRepo.save(challenge);

        // TODO: integrate SMS/Email provider here (don’t send raw code in prod logs)
        log.info("OTP created for user={} channel={} dest={} txnId={}",
                req.getUserId(), req.getChannel(), req.getDestination(), challenge.getTxnId());
        log.info("DEV ONLY OTP for txnId={} is {}", challenge.getTxnId(), code);
        return new CreateOtpResult(
                challenge.getTxnId(),
                challenge.getExpiresAt(),
                devReturnCode ? code : null
        );
    }

    private String generateNumericCode(int length) {
        // 10^len space, leading zeros allowed
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(secureRandom.nextInt(10));
        return sb.toString();
    }

    // === response type
    @lombok.Value
    public static class CreateOtpResult {
        UUID txnId;
        Instant expiresAt;
        String devCode;  // null when devReturnCode=false
    }

    // Lightweight response type for controller
    @lombok.Value
    public static class ConfirmOtpResult {
        UUID userId;
        UUID otpTxnId;
        boolean alreadyVerified;

        public static ConfirmOtpResult success(UUID uid, UUID txn, boolean already) {
            return new ConfirmOtpResult(uid, txn, already);
        }


    }
}