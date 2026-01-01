package com.womensafety.authservice.otp;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class OtpCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private OtpChannel channel;   // PHONE or EMAIL

    // required destination (phone/email). Relax if you store it elsewhere.
    @NotBlank
    @Size(max = 128)
    private String destination;
}