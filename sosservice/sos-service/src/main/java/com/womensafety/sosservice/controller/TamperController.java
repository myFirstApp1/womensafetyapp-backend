package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.TamperAnalysisResult;
import com.womensafety.sosservice.dto.TamperDetectionRequest;
import com.womensafety.sosservice.service.risk.TamperDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/tamper")
public class TamperController {

    private final TamperDetectionService tamperDetectionService;

    @PostMapping
    public ResponseEntity<TamperAnalysisResult> testTamper(
            @RequestBody TamperDetectionRequest request
    ) {

        return ResponseEntity.ok(
                tamperDetectionService.analyze(
                        request.getDeviceId(),
                        request.isStrapCut(),
                        request.isDeviceOpened(),
                        request.isSensorDisabled(),
                        request.isFirmwareModified(),
                        request.isBluetoothJammed(),
                        request.isGpsJammed(),
                        request.isPowerDisconnected(),
                        request.isDeviceDestroyed()
                )
        );
    }
}
