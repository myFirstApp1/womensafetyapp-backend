package com.womensafety.userservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 Link to Auth Service user_id
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    private String name;

    private String phone;

    private String address;

    @Column(name = "is_verified", columnDefinition = "bit(1) default 0")
    private boolean isVerified;

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
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();
}