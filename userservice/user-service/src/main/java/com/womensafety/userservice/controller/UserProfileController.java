package com.womensafety.userservice.controller;

import com.womensafety.userservice.dto.UserProfileResponse;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    // GET user profile
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(userProfileService.getUserProfileWithContacts(id));
    }

    // UPDATE or CREATE profile
    //@PutMapping("/{userId}")
    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfile> updateProfile(
            @PathVariable UUID userId,
            @RequestBody UserProfile updatedProfile) {
        return ResponseEntity.ok(userProfileService.updateProfile(userId, updatedProfile));
    }
}
