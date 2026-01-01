package com.womensafety.authservice.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ConfirmOtpRequest {

    @NotBlank
    private String txnId; // issued when OTP was created

    // 4–8 digits — adjust if your OTP length differs
    @NotBlank
    @Pattern(regexp = "^[0-9]{4,8}$", message = "OTP must be 4-8 digits")
    private String code;
}