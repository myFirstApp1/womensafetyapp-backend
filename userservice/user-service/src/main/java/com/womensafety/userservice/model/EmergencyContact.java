package com.womensafety.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "emergency_contacts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_profile_id", "phone_number"})
        }
)

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 20)
    private String phoneNumber;

    private String relation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}