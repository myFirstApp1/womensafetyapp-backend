package com.womensafety.notificationservice.controller;

import com.womensafety.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

   /* @GetMapping("/test-email")
    public String sendTestEmail(@RequestParam String to) {
        String subject = "Test Email from Women Safety App";
        String body = "Hello! This is a test email sent via Mailtrap.\n\nRegards,\nWomen Safety App Team";

        emailService.sendEmail(to, subject, body);

        return "Email sent to " + to + " (check Mailtrap inbox)";
    }*/

    @GetMapping("/send-test-email")
    public String sendTestEmail(@RequestParam String to) {
        emailService.sendEmail(
                to,
                "Test Mail from Women Safety App",
                "Hello! This is a test email."
        );
        return "Email sent to " + to;
    }
}