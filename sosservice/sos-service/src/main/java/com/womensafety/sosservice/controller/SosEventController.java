package com.womensafety.sosservice.controller;

import com.womensafety.sosservice.service.sos.SosEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/sos")
@RequiredArgsConstructor
public class SosEventController {

    private final SosEventService service;

    @PostMapping("/event")
    public ResponseEntity<Void> event(
            @RequestParam UUID userId,
            @RequestParam String event,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {

        service.processEvent(
                userId,
                event,
                lat,
                lng
        );

        return ResponseEntity.ok().build();
    }
}