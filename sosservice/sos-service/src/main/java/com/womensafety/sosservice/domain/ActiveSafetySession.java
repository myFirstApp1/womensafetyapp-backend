package com.womensafety.sosservice.domain;

import com.womensafety.sosservice.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "active_safety_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveSafetySession {

    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "device_id")
    private String deviceId;

    // 🔹 Heartbeat tracking
    @Column(name = "last_ping_time", nullable = false)
    private LocalDateTime lastPingTime;

    // 🔹 Battery info
    @Column(name = "battery_level")
    private Integer batteryLevel;

    // 🔹 Location
    @Column(name = "last_latitude", precision = 10, scale = 6)
    private BigDecimal lastLatitude;

    @Column(name = "last_longitude", precision = 10, scale = 6)
    private BigDecimal lastLongitude;

    // 🔹 Emergency flags
    @Column(name = "emergency_triggered")
    @Builder.Default
    private Boolean emergencyTriggered = false;

    @Column(name = "emergency_contact_notified")
    @Builder.Default
    private Boolean emergencyContactNotified = false;

    // 🔹 Session start
    @Column(name = "session_start_time")
    private LocalDateTime sessionStartTime;

    // =========================
    // 🚀 NEW FIELDS (CORE LOGIC)
    // =========================

    // 🔥 Main state machine
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status;

    // 🔹 Why paused
    @Enumerated(EnumType.STRING)
    @Column(name = "pause_type")
    private PauseType pauseType;

    // 🔹 Auto resume support
    @Column(name = "auto_resume_at")
    private LocalDateTime autoResumeAt;

    // 🔹 Device worn or not
    @Column(name = "is_device_worn")
    @Builder.Default
    private Boolean isDeviceWorn = true;

    // 🔹 Bluetooth tracking
    @Column(name = "last_bluetooth_seen_at")
    private LocalDateTime lastBluetoothSeenAt;

    @Column(name = "warning_triggered_at")
    private LocalDateTime warningTriggeredAt;

    @Column(name = "last_heart_rate")
    private Integer lastHeartRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "confirmation_status")
    private ConfirmationStatus confirmationStatus;

    @Column(name = "movement_score")
    private Integer movementScore;

    @Column(name = "tracking_id")
    private String trackingId;

    @Version
    private Long version;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_mode")
    private CommunicationMode communicationMode;

    @Column(name = "communication_failure_count")
    private Integer communicationFailureCount;

    @Column(name = "last_communication_attempt")
    private LocalDateTime lastCommunicationAttempt;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_gps_status")
    private GpsStatus lastGpsStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "last_off_body_event")
    private OffBodyEventType lastOffBodyEvent;
    @Enumerated(EnumType.STRING)
    @Column(name = "pre_alert_status")
    private PreAlertStatus preAlertStatus;
    @Column(name = "pre_alert_started_at")
    private LocalDateTime preAlertStartedAt;
    // =========================
    // 🧠 DEFAULT HELPER METHODS
    // =========================

    public boolean isActive() {
        return SessionStatus.ACTIVE.equals(this.status);
    }

    public boolean isPaused() {
        return SessionStatus.PAUSED_MANUAL.equals(this.status)
                || SessionStatus.PAUSED_OFF_BODY.equals(this.status);
    }

    public boolean isInDanger() {
        return SessionStatus.IN_DANGER.equals(this.status);
    }
}