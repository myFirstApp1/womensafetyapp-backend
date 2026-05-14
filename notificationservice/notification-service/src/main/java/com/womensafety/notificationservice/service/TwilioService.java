package com.womensafety.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwilioService {

    @Value("${notification.mode:mock}")
    private String mode;

    @Value("${twilio.fromNumber:MOCK}")
    private String fromNumber;

    // 📩 SMS
    public void sendSMS(String to, String body) {

        if ("mock".equalsIgnoreCase(mode)) {
            log.info("🚨 MOCK SMS → To: {} | Message: {}", to, body);
            return;
        }

        com.twilio.rest.api.v2010.account.Message message =
                com.twilio.rest.api.v2010.account.Message.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(fromNumber),
                        body
                ).create();

        log.info("SMS sent to {} sid={}", to, message.getSid());
    }

    // 📞 Voice
    public void makeCall(String to, String messageText) {

        if ("mock".equalsIgnoreCase(mode)) {
            log.info("📞 MOCK CALL → To: {} | Message: {}", to, messageText);
            return;
        }

        String twiml = "<Response><Say>" + messageText + "</Say></Response>";

        com.twilio.rest.api.v2010.account.Call call =
                com.twilio.rest.api.v2010.account.Call.creator(
                        new com.twilio.type.PhoneNumber(to),
                        new com.twilio.type.PhoneNumber(fromNumber),
                        new com.twilio.type.Twiml(twiml)
                ).create();

        log.info("Call placed to {} sid={}", to, call.getSid());
    }

}