package com.womensafety.userservice.service;

import com.womensafety.userservice.dto.EmergencyContactResponse;
import com.womensafety.userservice.dto.UserProfileResponse;
import com.womensafety.userservice.exception.ResourceNotFoundException;
import com.womensafety.userservice.exception.ValidationException;
import com.womensafety.userservice.model.EmergencyContact;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse getUserProfileWithContacts(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        return UserProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .avatar(profile.getAvatar())
                .address(profile.getAddress())
                .profilePictureUrl(profile.getProfilePictureUrl())
               // .isVerified(profile.isVerified())
                .emergencyContacts(
                        profile.getEmergencyContacts().stream()
                                .map(c -> EmergencyContactResponse.builder()
                                        .id(c.getId())
                                        .name(c.getName())
                                        .phoneNumber(c.getPhoneNumber())
                                        .relation(c.getRelation())
                                        .build()
                                ).toList()
                )
                .build();
    }
    @Transactional
    public UserProfile updateProfile(UUID userId, UserProfile updated) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));
        System.out.println("================================");
        System.out.println("Avatar received = " + updated.getAvatar());
        System.out.println(updated);
        System.out.println("================================");

        profile.setName(updated.getName());
        profile.setPhone(updated.getPhone());
        profile.setAddress(updated.getAddress());
        profile.setAvatar(updated.getAvatar());
        profile.setProfilePictureUrl(updated.getProfilePictureUrl());
        profile.setProfilePicturePath(updated.getProfilePicturePath());
        //profile.setVerified(updated.isVerified());

        if (updated.getEmergencyContacts() != null && !updated.getEmergencyContacts().isEmpty()) {
            // business rules: 1 ≤ contacts ≤ 15
            if (updated.getEmergencyContacts().size() > 15) {
                throw new ValidationException("Cannot add more than 15 emergency contacts");
            }
            profile.getEmergencyContacts().clear();
            for (EmergencyContact contact : updated.getEmergencyContacts()) {
                contact.setUserProfile(profile); // link back
                profile.getEmergencyContacts().add(contact);

            }
        }

            return userProfileRepository.save(profile);
    }
    public String uploadProfilePicture(
            UUID userId,
            MultipartFile file
    ) throws IOException {

        UserProfile profile =
                userProfileRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Profile not found"
                                )
                        );

        String fileName =
                UUID.randomUUID()
                        + "_"
                        + file.getOriginalFilename();

        Path uploadDir =
                Paths.get(
                        "uploads/profile"
                );

        Files.createDirectories(
                uploadDir
        );

        Path target =
                uploadDir.resolve(
                        fileName
                );

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        profile.setProfilePicturePath(
                target.toString()
        );

        profile.setProfilePictureUrl(
                "/uploads/profile/" + fileName
        );

        userProfileRepository.save(
                profile
        );

        return profile.getProfilePictureUrl();
    }
}