package com.womensafety.userservice.controller;

import com.womensafety.userservice.dto.EmergencyContactDto;
import com.womensafety.userservice.dto.EmergencyContactRequestDto;
import com.womensafety.userservice.service.EmergencyContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/contacts")
@RequiredArgsConstructor
public class EmergencyContactController {

    private final EmergencyContactService contactService;

    @PostMapping
    public ResponseEntity<EmergencyContactDto> addContact(
            @PathVariable Long userId,
            @RequestBody EmergencyContactRequestDto req) {
        return ResponseEntity.ok(contactService.addContact(userId, req));
    }

    @GetMapping
    public ResponseEntity<List<EmergencyContactDto>> listContacts(@PathVariable Long userId) {
        return ResponseEntity.ok(contactService.listContacts(userId));
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<EmergencyContactDto> updateContact(
            @PathVariable Long userId,
            @PathVariable Long contactId,
            @RequestBody EmergencyContactRequestDto req) {
        return ResponseEntity.ok(contactService.updateContact(contactId, req));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long userId,
            @PathVariable Long contactId) {
        contactService.deleteContact(userId,contactId);
        return ResponseEntity.noContent().build();
    }
}
