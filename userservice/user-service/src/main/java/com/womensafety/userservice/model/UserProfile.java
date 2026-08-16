package com.womensafety.userservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // 🔑 Link to Auth Service user_id
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    private String address;
    @Column(name = "avatar", nullable = false)
    private String avatar;
    @Column(name = "profile_picture_url", length = 2048)
    private String profilePictureUrl;
    @Column(name = "profile_picture_path", length = 512)
    private String profilePicturePath;
    @OneToMany(
            mappedBy = "userProfile",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    // helper to maintain bidirectional consistency
    public void addEmergencyContact(EmergencyContact contact) {
        contact.setUserProfile(this);
        this.emergencyContacts.add(contact);
    }

    public void clearEmergencyContacts() {
        for (EmergencyContact contact : this.emergencyContacts) {
            contact.setUserProfile(null);
        }
        this.emergencyContacts.clear();
    }
}