package com.womensafety.authservice.otp;

import com.womensafety.authservice.advice.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @PostMapping("/resend")
    public ResponseEntity<ResponseWrapper<ResendOtpResponse>> resendOtp(
            @Valid @RequestBody ResendOtpRequest request) {

        ResendOtpResponse response = otpService.resendOtp(request);
        return ResponseEntity.ok(ResponseWrapper.success("OTP resent successfully", response));
    }

    @lombok.Value
    static class VerifyResponse {
        String message;
        UUID userId;
        UUID otpTxnId;
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
        UUID txnId;
        String expiresAt;
        String devOtp;  // will be null if app.otp.dev-return-code=false
    }
}