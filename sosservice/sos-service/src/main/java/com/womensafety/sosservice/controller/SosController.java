package com.womensafety.sosservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womensafety.sosservice.dto.NotificationRequest;
import com.womensafety.sosservice.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final NotificationProducer producerService;


    @PostMapping("/trigger")
    public ResponseEntity<String> triggerSos(@RequestBody NotificationRequest request) {
        System.out.println("Incoming request: " + request);
        try {
            producerService.sendNotification(request);
            return ResponseEntity.ok("SOS Triggered and sent to Kafka");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }

    }
}