package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.dto.HeartbeatPacket;
import com.womensafety.sosservice.dto.VitalsUpdateRequest;
import com.womensafety.sosservice.service.HeartbeatCheckService;
import com.womensafety.sosservice.service.WearableHeartbeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final HeartbeatCheckService heartbeatCheckService;
    private final WearableHeartbeatService wearableHeartbeatService;

    // =========================
    // BLUETOOTH PING
    // =========================
    @PutMapping("/ping-bluetooth/{userId}")
    public ResponseEntity<String> updateBluetoothPing(@PathVariable UUID userId) {
        heartbeatCheckService.updateBluetoothPing(userId);
        return ResponseEntity.ok("Bluetooth ping updated");
    }

    // =========================
    // HEART RATE / VITALS
    // =========================
    @PostMapping("/vitals/update")
    public ResponseEntity<String> updateVitals(
            @Valid @RequestBody VitalsUpdateRequest request
           ) {
        heartbeatCheckService.updateVitals(
                request.userId(),
                request.heartRate(),
                request.movementScore()
        );
        return ResponseEntity.ok("Vitals updated");
    }

    @PostMapping("/off-body/{userId}")
    public ResponseEntity<String> markDeviceOffBody(
            @PathVariable UUID userId) {
        heartbeatCheckService.markDeviceOffBody(userId);
        return ResponseEntity.ok("Device marked as OFF_BODY");
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(
            @RequestBody HeartbeatPacket packet
    ) {
        wearableHeartbeatService.processHeartbeat(
                packet
        );
        return ResponseEntity.ok("Heartbeat Received");
    }
}