/*
package com.womensafety.notificationservice.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.whatsapp.from}")
    private String fromNumber; // Twilio sandbox number

    public void sendWhatsAppMessage(String toNumber, String body) {
        try {
            Message message =  Message.creator(
                    new PhoneNumber("whatsapp:" + toNumber),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
            System.out.println("📬 Twilio SID: " + message.getSid());
            System.out.println("📡 Twilio Status: " + message.getStatus());

            System.out.println("WhatsApp message sent to " + toNumber);
        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message: " + e.getMessage());
        }
    }
}*/
