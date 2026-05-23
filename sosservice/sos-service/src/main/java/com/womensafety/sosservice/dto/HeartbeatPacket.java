package com.womensafety.sosservice.dto;

import lombok.Data;

@Data
public class HeartbeatPacket {

    private String deviceId;

    private String firmwareVersion;

    private Integer heartRate;

    private Integer hrv;

    private Integer movementScore;

    private Integer batteryLevel;

    private Boolean deviceWorn;

    private Boolean bluetoothConnected;

    private Double latitude;

    private Double longitude;

    private Long deviceTimestamp;
}