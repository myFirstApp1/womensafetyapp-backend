package com.womensafety.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String address;
}
