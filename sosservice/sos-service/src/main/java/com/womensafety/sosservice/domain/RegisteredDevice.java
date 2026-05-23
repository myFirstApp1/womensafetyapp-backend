package com.womensafety.sosservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "registered_devices")
@Data
public class RegisteredDevice {

    @Id
    private String deviceId;

    private String firmwareVersion;

    private LocalDateTime lastHeartbeatAt;

    private Integer batteryLevel;

    private Boolean active;
}
