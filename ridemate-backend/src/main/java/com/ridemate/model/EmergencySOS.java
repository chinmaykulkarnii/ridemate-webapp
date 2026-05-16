package com.ridemate.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergency_sos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencySOS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private Double latitude;
    private Double longitude;

    private String location;

    @Column(length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SOSStatus status;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    private String resolvedBy;  // Admin or support user ID

    public enum SOSStatus {
        ACTIVE,
        RESOLVED,
        FALSE_ALARM
    }
}
