package com.womensafety.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    @Value("${app.email.mock:true}")
    private boolean mock;

    public void sendEmail(String to, String subject, String text) {
        if (mock) {
            log.info("MOCK LOGGER EMAIL → To: {}, Subject: {}, Body: {}", to, subject, text);
            return;
        }

        /*try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }*/
    }

    public void sendVerificationEmail(String to, String verificationUrl) {

        /*try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Verify your account - Women Safety App");
            message.setText("Click the link to verify your account: " + verificationUrl);

            mailSender.send(message);
            log.info("Verification email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}", to, e);
        }*/
        if (mock) {
            log.info("MOCK EMAIL → To: {}, Subject: {}, Body: {}", to, "Verify your account - Women Safety App", verificationUrl);
            return;
        }


    }

}