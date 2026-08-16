package com.womensafety.userservice.controller;

import com.womensafety.userservice.dto.EmergencyContactDto;
import com.womensafety.userservice.dto.EmergencyContactRequestDto;
import com.womensafety.userservice.service.EmergencyContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/contacts")
@RequiredArgsConstructor
@Slf4j
public class EmergencyContactController {

    private final EmergencyContactService contactService;


    @PostMapping("/{userId}")
    public ResponseEntity<EmergencyContactDto> addContact(
            @PathVariable UUID userId,
            @RequestBody EmergencyContactRequestDto req) {
        log.info("Emergency Contact is invoked");
        return ResponseEntity.ok(contactService.addContact(userId, req));
    }

    @GetMapping("/public/{userId}/numbers")
    public ResponseEntity<List<String>> listEmergencyContacts(@PathVariable UUID userId) {
        log.info("List Emergency Contact is invoked");
        List<String> contacts = contactService.getEmergencyContactNumbers(userId);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<EmergencyContactDto>> listContacts(@PathVariable UUID userId) {
        return ResponseEntity.ok(contactService.listContacts(userId));
    }

    @PutMapping("/{userId}/{contactId}")
    public ResponseEntity<EmergencyContactDto> updateContact(
            @PathVariable UUID userId,
            @PathVariable Long contactId,
            @RequestBody EmergencyContactRequestDto req) {

        return ResponseEntity.ok(
                contactService.updateContact(userId, contactId, req));
    }

    @DeleteMapping("/{userId}/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable UUID userId,
            @PathVariable Long contactId) {
        contactService.deleteContact(userId,contactId);
        return ResponseEntity.noContent().build();
    }


}
