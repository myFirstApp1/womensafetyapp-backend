package com.womensafety.authservice.otp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@Valid @RequestBody ConfirmOtpRequest req) {
        var result = otpService.confirmOtp(req);
        return ResponseEntity.ok(new VerifyResponse(
                "OTP verified",
                result.getUserId(),
                result.getOtpTxnId(),
                result.isAlreadyVerified()
        ));
    }

    @lombok.Value
    static class VerifyResponse {
        String message;
        Long userId;
        String otpTxnId;
        boolean alreadyVerified;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateResponse> create(@Valid @RequestBody OtpCreateRequest req) {
        var result = otpService.createOtp(req);
        return ResponseEntity.ok(new CreateResponse(
                "OTP created",
                result.getTxnId(),
                result.getExpiresAt().toString(),
                result.getDevCode()    // null in prod
        ));
    }

    @lombok.Value
    static class CreateResponse {
        String message;
        String txnId;
        String expiresAt;
        String devOtp;  // will be null if app.otp.dev-return-code=false
    }
}