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

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileResponse getUserProfileWithContacts(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        return UserProfileResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .isVerified(profile.isVerified())
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
    public UserProfile updateProfile(Long userId, UserProfile updated) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));


        profile.setName(updated.getName());
        profile.setPhone(updated.getPhone());
        profile.setAddress(updated.getAddress());
        profile.setProfilePictureUrl(updated.getProfilePictureUrl());
        profile.setProfilePicturePath(updated.getProfilePicturePath());
        profile.setVerified(updated.isVerified());

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
}