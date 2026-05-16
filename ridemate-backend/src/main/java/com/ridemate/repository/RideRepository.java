package com.ridemate.repository;

import com.ridemate.model.Ride;
import com.ridemate.model.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    List<Ride> findByDriverId(Long driverId);
    List<Ride> findByIsActive(Boolean isActive);

    @Query("SELECT r FROM Ride r WHERE r.isActive = true " +
            "AND r.origin LIKE %:origin% " +
            "AND r.destination LIKE %:destination% " +
            "AND r.departureTime >= :departureTime " +
            "AND r.availableSeats >= :seatsRequired")
    List<Ride> searchRides(@Param("origin") String origin,
                           @Param("destination") String destination,
                           @Param("departureTime") LocalDateTime departureTime,
                           @Param("seatsRequired") Integer seatsRequired);

    @Query("SELECT r FROM Ride r WHERE r.isActive = true " +
            "AND r.origin LIKE %:origin% " +
            "AND r.destination LIKE %:destination% " +
            "AND r.departureTime >= :departureTime " +
            "AND r.availableSeats >= :seatsRequired " +
            "AND (:vehicleType IS NULL OR r.vehicleType = :vehicleType) " +
            "AND (:maxPrice IS NULL OR r.pricePerSeat <= :maxPrice)")
    List<Ride> searchRidesWithFilters(@Param("origin") String origin,
                                      @Param("destination") String destination,
                                      @Param("departureTime") LocalDateTime departureTime,
                                      @Param("seatsRequired") Integer seatsRequired,
                                      @Param("vehicleType") VehicleType vehicleType,
                                      @Param("maxPrice") Double maxPrice);

    // Advanced filtering
    @Query("SELECT r FROM Ride r WHERE r.isActive = true " +
            "AND (:origin IS NULL OR r.origin LIKE %:origin%) " +
            "AND (:destination IS NULL OR r.destination LIKE %:destination%) " +
            "AND r.departureTime BETWEEN :startTime AND :endTime " +
            "AND r.availableSeats >= :seatsRequired " +
            "AND (:vehicleType IS NULL OR r.vehicleType = :vehicleType) " +
            "AND (:maxPrice IS NULL OR r.pricePerSeat <= :maxPrice) " +
            "AND (:womenOnly IS NULL OR r.womenOnly = :womenOnly) " +
            "AND (:petFriendly IS NULL OR r.petFriendly = :petFriendly) " +
            "AND (:luggageAllowed IS NULL OR r.luggageAllowed = :luggageAllowed)")
    List<Ride> searchRidesAdvanced(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("seatsRequired") Integer seatsRequired,
            @Param("vehicleType") VehicleType vehicleType,
            @Param("maxPrice") Double maxPrice,
            @Param("womenOnly") Boolean womenOnly,
            @Param("petFriendly") Boolean petFriendly,
            @Param("luggageAllowed") Boolean luggageAllowed
    );

    // For surge pricing calculation
    long countByOriginAndDestinationAndDepartureTimeBetween(
            String origin, String destination,
            LocalDateTime startTime, LocalDateTime endTime
    );

    // Find recurring rides
    List<Ride> findByIsRecurringTrue();
}