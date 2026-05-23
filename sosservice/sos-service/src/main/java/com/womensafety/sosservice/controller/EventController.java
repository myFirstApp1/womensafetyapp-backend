package com.womensafety.sosservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    @PostMapping
    public ResponseEntity<String> handleEvent(
            @RequestParam UUID userId,
            @RequestParam String event
    ) {

        log.info("EVENT_RECEIVED | userId={} | event={}",
                userId,
                event);

        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}