package com.womensafety.sosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String eventId;
    private String timestamp;
    private User user;
    private List<Contact> contacts;
    private String message;
    private Location location;
    private List<String> channels;
    private String priority;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String userId;
        private String name;
        private String phone;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        private String name;
        private String phone;
        private String email;
        private List<String> preferredChannels;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private double latitude;
        private double longitude;
        private String address;
    }
}