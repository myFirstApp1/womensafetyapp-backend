/*
package com.womensafety.authservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class TestKafkaController {
    private final VerificationPublisher publisher;

    @PostMapping("/test")
    public ResponseEntity<?> send(@RequestParam String id, @RequestParam String email) {
        publisher.publish(id, email);
        return ResponseEntity.ok().build();
    }
}*/
