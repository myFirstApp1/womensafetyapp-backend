package com.womensafety.authservice.otp;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpCreateRequest {

    @NotNull
    private UUID userId;

    @NotNull
    private OtpChannel channel;   // PHONE or EMAIL

    // required destination (phone/email). Relax if you store it elsewhere.
    @NotBlank
    @Size(max = 128)
    private String destination;
}