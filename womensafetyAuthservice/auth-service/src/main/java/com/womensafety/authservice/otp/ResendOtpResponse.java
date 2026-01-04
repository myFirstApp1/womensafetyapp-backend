package com.womensafety.authservice.otp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ResendOtpResponse {
    private UUID userId;
    private UUID txnId;
}