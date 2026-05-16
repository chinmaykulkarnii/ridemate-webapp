package com.ridemate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridemate.model.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "rides")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    // Geolocation coordinates for mapping
    private Double originLat;
    private Double originLng;
    private Double destinationLat;
    private Double destinationLng;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Double pricePerSeat;

    private String vehicleModel;
    private String vehicleNumber;
    private String additionalInfo;

    // Distance and duration
    private Double distance;  // in km
    private Integer duration;  // in minutes

    // Advanced features
    @Builder.Default
    @Column(nullable = false)
    private Boolean womenOnly = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean petFriendly = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean luggageAllowed = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean verifiedDriversOnly = false;

    // Recurring ride information
    @Builder.Default
    @Column(nullable = false)
    private Boolean isRecurring = false;

    @ElementCollection
    @CollectionTable(name = "ride_recurring_days", joinColumns = @JoinColumn(name = "ride_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> recurringDays;

    private LocalDateTime recurringEndDate;

    // Price surge indicator
    @Builder.Default
    @Column(nullable = false)
    private Double surgeFactor = 1.0;  // 1.0 = no surge, 1.5 = 50% surge, etc.

    // Tracking
    @Builder.Default
    @Column(nullable = false)
    private Boolean trackingEnabled = false;

    private Double currentLat;
    private Double currentLng;
    private LocalDateTime lastLocationUpdate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    // Helper method to calculate final price with surge
    public Double getFinalPricePerSeat() {
        return pricePerSeat * surgeFactor;
    }

    // Helper method to check if ride is full
    public boolean isFull() {
        return availableSeats <= 0;
    }
}