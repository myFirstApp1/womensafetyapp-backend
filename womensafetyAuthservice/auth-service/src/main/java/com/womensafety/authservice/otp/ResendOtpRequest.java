package com.womensafety.authservice.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResendOtpRequest {

    @NotBlank
    @jakarta.validation.constraints.Email
    private String email;

    @NotNull
    private OtpChannel channel; // EMAIL | PHONE
}