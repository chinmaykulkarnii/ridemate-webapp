package com.ridemate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column
    private String password;  // Nullable for OAuth users

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;
    private String profilePicture;

    @Column
    private String provider;  // LOCAL, GOOGLE, GITHUB

    @Column
    private String providerId;  // OAuth provider's user ID

    // User Roles (can have multiple roles)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    // Gender for women-only rides feature
    @Enumerated(EnumType.STRING)
    private Gender gender;

    // Language preference
    @Builder.Default
    @Column(length = 10)
    private String preferredLanguage = "en";  // en, hi, mr

    // Verification fields
    @Builder.Default
    @Column(nullable = false)
    private Boolean isEmailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPhoneVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isIdVerified = false;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Gamification
    @Builder.Default
    @Column(nullable = false)
    private Integer points = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "badge")
    private Set<String> badges;

    // Referral code
    @Column(unique = true)
    private String referralCode;

    private Long referredBy;  // User ID who referred this user

    // Rating fields
    @Builder.Default
    @Column(nullable = false)
    private Double averageRating = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalRatings = 0;

    // Analytics fields
    @Builder.Default
    @Column(nullable = false)
    private Integer totalRidesOffered = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalRidesTaken = 0;

    @Builder.Default
    @Column(nullable = false)
    private Double totalDistanceTraveled = 0.0;  // in km

    @Builder.Default
    @Column(nullable = false)
    private Double totalCarbonSaved = 0.0;  // in kg

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
    private List<Ride> ridesOffered;

    @JsonIgnore
    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    // Helper method to check if user has a specific role
    public boolean hasRole(UserRole role) {
        return roles != null && roles.contains(role);
    }

    // Helper method to add a role
    public void addRole(UserRole role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}