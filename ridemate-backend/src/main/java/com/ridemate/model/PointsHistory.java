package com.ridemate.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PointsAction action;

    private String description;

    @ManyToOne
    @JoinColumn(name = "related_ride_id")
    private Ride relatedRide;

    @ManyToOne
    @JoinColumn(name = "related_booking_id")
    private Booking relatedBooking;

    private Long referredUserId;  // If action is REFERRAL

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum PointsAction {
        RIDE_COMPLETED,      // Points for completing a ride
        RIDE_OFFERED,        // Points for offering a ride
        REFERRAL,            // Points for referring a user
        RATING_GIVEN,        // Points for rating a ride
        PROFILE_COMPLETED,   // Points for completing profile
        VERIFICATION,        // Points for verification
        ECO_WARRIOR,         // Bonus for saving carbon
        FIRST_RIDE,          // Bonus for first ride
        LOYALTY_BONUS,       // Loyalty points
        REDEEMED             // Points redeemed (negative)
    }
}
