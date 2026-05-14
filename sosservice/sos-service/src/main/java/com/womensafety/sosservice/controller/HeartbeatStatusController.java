package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.domain.ActiveSafetySession;
import com.womensafety.sosservice.service.HeartbeatCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/heartbeat")
@RequiredArgsConstructor
public class HeartbeatStatusController {

    private final HeartbeatCheckService heartbeatCheckService;

    /**
     * Start protection mode for a user.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startProtection(@RequestParam UUID userId) {
        log.info("HEARTBEAT_API | start | userId={}", userId);
        heartbeatCheckService.startProtection(userId);
        return ResponseEntity.ok("Protection started for user: " + userId);
    }

    /**
     * Send a heartbeat ping to keep protection session alive.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<String> heartbeat(
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer battery,
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) BigDecimal lon) {
        log.debug("HEARTBEAT_API | heartbeat | userId={}", userId);
        heartbeatCheckService.ping(userId);
        return ResponseEntity.ok("Heartbeat updated for user: " + userId);
    }
    /**
     * Stop protection mode for a user.
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopProtection(@RequestParam UUID userId) {
        log.info("HEARTBEAT_API | stop | userId={}", userId);
        heartbeatCheckService.stopProtection(userId);
        return ResponseEntity.ok("Protection stopped for user: " + userId);
    }

    /**
     * Check if a user is currently protected.
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus(@RequestParam UUID userId) {
        boolean isProtected = heartbeatCheckService.isUserProtected(userId);
        log.debug("HEARTBEAT_API | status | userId={} | protected={}", userId, isProtected);
        return ResponseEntity.ok(isProtected ? "Protected" : "Not Protected");
    }

    /**
     * Get count of currently protected users.
     */
    @GetMapping("/protected-count")
    public ResponseEntity<Integer> getProtectedCount() {
        int count = heartbeatCheckService.getProtectedUsers().size();
        log.debug("HEARTBEAT_API | protected-count | count={}", count);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/stale-users")
    public List<ActiveSafetySession> getStaleUsers() {
        return heartbeatCheckService.getStaleUsers();
    }

    @GetMapping("/sessions")
    public List<ActiveSafetySession> getAllSessions() {
        return heartbeatCheckService.getProtectedUsers();
    }

    @PostMapping("/pause")
    public ResponseEntity<String> pause(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "30") int minutes) {

        heartbeatCheckService.pauseProtection(userId, minutes);

        return ResponseEntity.ok("Protection paused for " + minutes + " minutes");
    }

    @PostMapping("/resume")
    public ResponseEntity<String> resume(@RequestParam UUID userId) {

        heartbeatCheckService.resumeProtection(userId);

        return ResponseEntity.ok("Protection resumed");
    }

    @PostMapping("/confirm-safe")
    public ResponseEntity<String> confirmSafe(@RequestParam UUID userId) {

        heartbeatCheckService.confirmUserSafe(userId);

        return ResponseEntity.ok("User marked safe");
    }
}
