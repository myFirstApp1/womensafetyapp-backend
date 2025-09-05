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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmergencyContactService {

    private final EmergencyContactRepository contactRepository;
    private final UserProfileRepository userProfileRepository;

    public EmergencyContactDto addContact(Long userId, EmergencyContactRequestDto req) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        // Rule 1: Max 15 contacts
        List<EmergencyContact> existingContacts = contactRepository.findByUserProfileId(profile.getId());
        // Rule: Max 15 contacts
        if (existingContacts.size() >= 15) {
            throw new ValidationException("Cannot add more than 15 emergency contacts");
        }

        // Rule: Duplicate phone not allowed
        boolean duplicatePhone = existingContacts.stream()
                .anyMatch(c -> c.getPhoneNumber().equals(req.getPhoneNumber()));
        if (duplicatePhone) {
            throw new ValidationException("Phone number already exists in emergency contacts");
        }
        EmergencyContact contact = EmergencyContact.builder()
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .relation(req.getRelation())
                .userProfile(profile)
                .build();

        EmergencyContact saved = contactRepository.save(contact);
        return mapToDto(saved);
    }

    public void deleteContact(Long userId, Long contactId) {
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

    public List<EmergencyContactDto> listContacts(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for userId: " + userId));

        return contactRepository.findByUserProfileId(profile.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EmergencyContactDto updateContact(Long contactId, EmergencyContactRequestDto req) {
        EmergencyContact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        contact.setName(req.getName());
        contact.setPhoneNumber(req.getPhoneNumber());
        contact.setRelation(req.getRelation());

        EmergencyContact updated = contactRepository.save(contact);
        return mapToDto(updated);
    }

    private EmergencyContactDto mapToDto(EmergencyContact c) {
        return new EmergencyContactDto(c.getId(), c.getName(), c.getPhoneNumber(), c.getRelation());
    }
}
