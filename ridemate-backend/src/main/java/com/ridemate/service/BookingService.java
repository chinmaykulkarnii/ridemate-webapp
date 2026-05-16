package com.ridemate.service;

import com.ridemate.dto.request.BookingRequest;
import com.ridemate.model.Booking;
import com.ridemate.model.Ride;
import com.ridemate.model.User;
import com.ridemate.model.enums.BookingStatus;
import com.ridemate.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RideService rideService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Booking createBooking(Long passengerId, BookingRequest request) {
        User passenger = userService.getUserById(passengerId);
        Ride ride = rideService.getRideById(request.getRideId());

        if (ride.getAvailableSeats() < request.getSeatsBooked()) {
            throw new RuntimeException("Not enough available seats");
        }

        Booking booking = Booking.builder()
                .ride(ride)
                .passenger(passenger)
                .seatsBooked(request.getSeatsBooked())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Send notification to driver
        notificationService.createNotification(
                ride.getDriver().getId(),
                "New Booking Request",
                passenger.getFirstName() + " " + passenger.getLastName() +
                        " has requested " + request.getSeatsBooked() + " seat(s) for your ride."
        );

        return savedBooking;
    }

    @Transactional
    public Booking confirmBooking(Long bookingId, Long driverId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getRide().getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Only the driver can confirm this booking");
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in pending status");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());

        // Update available seats
        rideService.updateAvailableSeats(
                booking.getRide().getId(),
                -booking.getSeatsBooked()
        );

        Booking savedBooking = bookingRepository.save(booking);

        // Send notification to passenger
        notificationService.createNotification(
                booking.getPassenger().getId(),
                "Booking Confirmed",
                "Your booking has been confirmed by the driver."
        );

        return savedBooking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);

        if (!booking.getPassenger().getId().equals(userId) &&
                !booking.getRide().getDriver().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        BookingStatus previousStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());

        // Restore available seats if booking was confirmed
        if (previousStatus == BookingStatus.CONFIRMED) {
            rideService.updateAvailableSeats(
                    booking.getRide().getId(),
                    booking.getSeatsBooked()
            );
        }

        Booking savedBooking = bookingRepository.save(booking);

        // Send notification
        Long notifyUserId = booking.getPassenger().getId().equals(userId)
                ? booking.getRide().getDriver().getId()
                : booking.getPassenger().getId();

        notificationService.createNotification(
                notifyUserId,
                "Booking Cancelled",
                "A booking has been cancelled."
        );

        return savedBooking;
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getBookingsByPassenger(Long passengerId) {
        return bookingRepository.findByPassengerId(passengerId);
    }

    public List<Booking> getBookingsByDriver(Long driverId) {
        return bookingRepository.findByRideDriverId(driverId);
    }

    public List<Booking> getBookingsByRide(Long rideId) {
        return bookingRepository.findByRideId(rideId);
    }
}