package com.womensafety.authservice.otp;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "otp_challenges",
        indexes = {
                @Index(name = "idx_otp_user_channel", columnList = "user_id, channel"),
                @Index(name = "idx_otp_txn", columnList = "txn_id", unique = true)
        }
)
public class OtpChallenge {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private OtpChannel channel; // PHONE or EMAIL

    // Phone number or email (optional to store)
    @Column(name = "destination", length = 128)
    private String destination;

    // Store a hash of the OTP, not the raw code
    @Column(name = "code_hash", nullable = false, length = 128)
    private String codeHash;

    @Column(name = "salt", nullable = false, length = 64)
    private String salt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private OtpStatus status;

    // For audit/tracing; we’ll also propagate this in the event as otpTxnId
    @Column(name = "txn_id", nullable = false,unique = true, columnDefinition = "BINARY(16)")
    private UUID txnId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isBlocked() {
        return attempts >= maxAttempts || status == OtpStatus.BLOCKED || status == OtpStatus.EXPIRED;
    }

    public void markVerified(Instant when) {
        this.status = OtpStatus.VERIFIED;
        this.verifiedAt = when;
    }

    public void markExpired() {
        this.status = OtpStatus.EXPIRED;
    }

    public void incAttempts() {
        this.attempts++;
    }
}