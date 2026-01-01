package com.womensafety.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyContactDto {
    private Long id;
    private String name;
    private String phoneNumber;
    private String relation;
}