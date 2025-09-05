package com.womensafety.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String profilePictureUrl;
    private Boolean isVerified;
    private List<EmergencyContactResponse> emergencyContacts;

}
