package com.womensafety.notificationservice.model;

import lombok.Data;

import java.util.List;

@Data
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
    public static class User {
        private String userId;
        private String name;
        private String phone;
        private String email;
    }

    @Data
    public static class Contact {
        private String name;
        private String phone;
        private String email;
        private List<String> preferredChannels;
    }

    @Data
    public static class Location {
        private double latitude;
        private double longitude;
        private String address;
    }
}