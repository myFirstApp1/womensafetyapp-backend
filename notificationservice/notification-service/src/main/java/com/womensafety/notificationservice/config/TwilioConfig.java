/*
package com.womensafety.notificationservice.config;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @PostConstruct
    public void init() {
        com.twilio.Twilio.init(accountSid, authToken);
    }
}*/
