package com.tl.womensafety.common.events;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserVerifiedEvent {
    private String eventId;       // UUID
    private String eventType;     // "USER_VERIFIED"
    private int version;          // 1
    private Long userId;
    private Verification verification;
    private Instant occurredAt;

    @Data
    public static class Verification {
        private String channel;   // "OTP"
        private String context;   // "PHONE" | "EMAIL"
        private String otpTxnId;
        private Instant verifiedAt;
    }

    public static UserVerifiedEvent from(Long userId, String otpTxnId, Instant ts) {
        UserVerifiedEvent e = new UserVerifiedEvent();
        e.eventId = UUID.randomUUID().toString();
        e.eventType = "USER_VERIFIED";
        e.version = 1;
        e.userId = userId;
        Verification v = new Verification();
        v.channel = "OTP";
        v.context = "PHONE";
        v.otpTxnId = otpTxnId;
        v.verifiedAt = ts;
        e.verification = v;
        e.occurredAt = ts;
        return e;
    }
}
