package com.womensafety.sosservice.controller;


import com.womensafety.sosservice.service.acknowledgement.AcknowledgementService;
import com.womensafety.sosservice.service.acknowledgement.DeliveryConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ack")
@RequiredArgsConstructor
public class AcknowledgementController {
    private final DeliveryConfirmationService deliveryConfirmationService;
    private final AcknowledgementService
            acknowledgementService;
    @PostMapping("/family")
    public ResponseEntity<Void> familyAck(
            @RequestParam String trackingId
    ) {

        deliveryConfirmationService
                .markDelivered(
                        trackingId
                );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/police")
    public ResponseEntity<Void> policeAck(
            @RequestParam String trackingId
    ) {

        acknowledgementService
                .markAcknowledged(
                        trackingId
                );

        return ResponseEntity.ok().build();
    }
}
