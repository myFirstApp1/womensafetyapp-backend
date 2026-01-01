package com.womensafety.sosservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;
    private final String baseUrl;

    public UserServiceClient(@Value("${user.service.base-url}") String baseUrl) {
        this.restClient = RestClient.create();
        this.baseUrl = baseUrl;
    }

    public List<String> getEmergencyContacts(String userId) {
        try {
            List<String> contacts = restClient.get()
                    .uri(baseUrl + "/public/" + userId + "/numbers")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});

            assert contacts != null;
            log.info(" Retrieved {} emergency contacts for userId={}", contacts.size(), userId);
            return contacts;
        } catch (Exception e) {
            log.error(" Failed to fetch contacts for userId={} | Reason={}", userId, e.getMessage());
            return List.of();
        }
    }
}