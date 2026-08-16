package com.womensafety.sosservice.dto;

import lombok.Data;

@Data
public class HeartbeatPacket {

    // Device Information
    private String deviceId;

    private String firmwareVersion;

    private Long deviceTimestamp;

    // Health
    private Integer heartRate;

    private Integer hrv;

    // Motion
    private Double movement;

    private Double speed;

    // Accelerometer
    private Double accelX;

    private Double accelY;

    private Double accelZ;

    // Gyroscope
    private Double gyroX;

    private Double gyroY;

    private Double gyroZ;

    // Device Status
    private Integer batteryLevel;

    private Boolean deviceWorn;

    private Boolean bluetoothConnected;

    // GPS
    private Double latitude;

    private Double longitude;

}