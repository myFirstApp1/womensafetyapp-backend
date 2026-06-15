package com.womensafety.sosservice.controller;


import com.womensafety.sosservice.service.communication.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosController {

    private final NotificationService notificationService;

    /**
     * Consolidated endpoint to trigger SOS notifications.
     * Optional `channel` can be used to indicate routing preference.
     */
    @PostMapping("/trigger/{userId}")
    public ResponseEntity<String> triggerSos(
            @PathVariable String userId,
            @RequestParam(name = "location") String currentLocation,
            @RequestParam(name = "channel", required = false) String channel,
            @RequestParam(name = "async", required = false, defaultValue = "false") boolean async
    ) {
        log.info("Incoming SOS for user {} at {} via {}", userId, currentLocation, channel == null ? "default" : channel);

        try {
            notificationService.sendAutomaticSos(userId, currentLocation, channel);

            if (async) {
                return ResponseEntity.accepted().build();
            }

            return ResponseEntity.ok("SOS Triggered and dispatched");

        } catch (Exception e) {
            log.error("Failed to trigger SOS for user={}", userId, e);
            return ResponseEntity.internalServerError().body("Error processing request");
        }
    }

}