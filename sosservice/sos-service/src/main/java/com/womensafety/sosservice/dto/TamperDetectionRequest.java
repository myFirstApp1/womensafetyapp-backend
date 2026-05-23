package com.womensafety.sosservice.dto;

import lombok.Data;

@Data
public class TamperDetectionRequest {

    private boolean strapCut;

    private boolean deviceOpened;

    private boolean sensorDisabled;

    private boolean firmwareModified;

    private boolean bluetoothJammed;

    private boolean gpsJammed;

    private boolean powerDisconnected;

    private boolean deviceDestroyed;
    private String deviceId;
}