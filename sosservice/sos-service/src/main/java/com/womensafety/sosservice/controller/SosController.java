package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.kafka.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final NotificationProducer producerService;


    @PostMapping("/trigger/{userId}")
    public ResponseEntity<String> triggerSosGeneric(@PathVariable String userId,
                                             @RequestParam(name = "location") String currentLocation) {
        log.info(" Incoming SOS for user {} at {}", userId, currentLocation);
        try {
            producerService.sendAutomaticSOS(userId,currentLocation);
            return ResponseEntity.ok("SOS Triggered and sent to Kafka");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

    @PostMapping("/trigger/whatsapp/{userId}")
    public ResponseEntity<String> triggerWhatsAppSOS(@PathVariable String userId,
                                             @RequestParam(name = "location") String currentLocation) throws ExecutionException, InterruptedException {
        log.info("📲 WhatsApp SOS triggered for user {} at {}", userId, currentLocation);
        producerService.sendAutomaticSOS(userId,currentLocation);
        return ResponseEntity.ok(" SOS alert triggered for user " + userId);
    }

    @PostMapping("/notify/{userId}")
    public ResponseEntity<Void> notifyUser(@PathVariable String userId,
                                       @RequestParam(name = "location") String currentLocation) throws ExecutionException, InterruptedException {
        producerService.sendAutomaticSOS(userId,currentLocation);
        return ResponseEntity.accepted().build();
    }

}