package com.ridemate.repository;

import com.ridemate.model.Booking;
import com.ridemate.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerId(Long passengerId);
    List<Booking> findByRideId(Long rideId);
    List<Booking> findByRideDriverId(Long driverId);
    List<Booking> findByPassengerIdAndStatus(Long passengerId, BookingStatus status);
}