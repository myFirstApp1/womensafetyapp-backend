package com.womensafety.userservice.service;

import com.womensafety.userservice.dto.EmergencyContactDto;
import com.womensafety.userservice.dto.EmergencyContactRequestDto;
import com.womensafety.userservice.exception.ResourceNotFoundException;
import com.womensafety.userservice.exception.ValidationException;
import com.womensafety.userservice.model.EmergencyContact;
import com.womensafety.userservice.model.UserProfile;
import com.womensafety.userservice.repository.EmergencyContactRepository;
import com.womensafety.userservice.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository contactRepository;
    private final UserProfileRepository userProfileRepository;

    public EmergencyContactDto addContact(UUID userId, EmergencyContactRequestDto req) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // Rule 1: Max 15 contacts
        List<EmergencyContact> existingContacts = contactRepository.findByUserProfileId(profile.getId());
        // Rule: Max 15 contacts
        if (existingContacts.size() >= 15) {
            throw new ValidationException("Cannot add more than 15 emergency contacts");
        }
        String normalizedPhone = normalizePhone(req.getPhoneNumber());
        // Rule: Duplicate phone not allowed
        boolean duplicatePhone = existingContacts.stream()
                .anyMatch(c -> c.getPhoneNumber().equals(req.getPhoneNumber()));
        if (duplicatePhone) {
            throw new ValidationException("Phone number already exists in emergency contacts");
        }
        EmergencyContact contact = EmergencyContact.builder()
                .name(req.getName())
                .phoneNumber(normalizedPhone)
                .relation(req.getRelation())
                .userProfile(profile)
                .build();

        EmergencyContact saved = contactRepository.save(contact);
        return mapToDto(saved);
    }

    public void deleteContact(UUID userId, Long contactId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        List<EmergencyContact> existingContacts = contactRepository.findByUserProfileId(profile.getId());
        if (existingContacts.size() <= 1) {
            throw new ValidationException("User must have at least 1 emergency contact");
        }

        EmergencyContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactRepository.delete(contact);
    }

    public List<EmergencyContactDto> listContacts(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        return contactRepository.findByUserProfileId(profile.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EmergencyContactDto updateContact(
            UUID userId,
            Long contactId,
            EmergencyContactRequestDto req) {

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        EmergencyContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        // Ownership check
        if (!contact.getUserProfile().getId().equals(profile.getId())) {
            throw new ValidationException("Cannot update contact not belonging to user");
        }

        String normalizedPhone = normalizePhone(req.getPhoneNumber());

        // Duplicate check excluding self
        boolean duplicate = contactRepository
                .findByUserProfileId(profile.getId())
                .stream()
                .anyMatch(c ->
                        !c.getId().equals(contactId) &&
                                c.getPhoneNumber().equals(normalizedPhone)
                );

        if (duplicate) {
            throw new ValidationException("Phone number already exists in emergency contacts");
        }

        contact.setName(req.getName());
        contact.setPhoneNumber(normalizedPhone);
        contact.setRelation(req.getRelation());

        EmergencyContact updated = contactRepository.save(contact);
        return mapToDto(updated);
    }

    private EmergencyContactDto mapToDto(EmergencyContact c) {
        return new EmergencyContactDto(c.getId(), c.getName(), c.getPhoneNumber(), c.getRelation());
    }

    public List<String> getEmergencyContactNumbers(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        return contactRepository.findByUserProfileId(profile.getId())
                .stream()
                .map(EmergencyContact::getPhoneNumber)
                .toList();
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            throw new ValidationException("Phone number is required");
        }

        phone = phone.replaceAll("\\s+", "");

        if (phone.startsWith("+91") && phone.length() == 13) {
            return phone;
        }

        if (phone.matches("\\d{10}")) {
            return "+91" + phone;
        }

        throw new ValidationException("Invalid phone number format");
    }
}
