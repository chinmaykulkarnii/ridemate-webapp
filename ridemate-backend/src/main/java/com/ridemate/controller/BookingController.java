package com.ridemate.controller;

import com.ridemate.dto.request.BookingRequest;
import com.ridemate.model.Booking;
import com.ridemate.model.User;
import com.ridemate.service.BookingService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request,
                                                 Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookingService.createBooking(user.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/passenger")
    public ResponseEntity<List<Booking>> getPassengerBookings(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingsByPassenger(user.getId()));
    }

    @GetMapping("/driver")
    public ResponseEntity<List<Booking>> getDriverBookings(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingsByDriver(user.getId()));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id,
                                                  Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookingService.confirmBooking(id, user.getId()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id,
                                                 Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(bookingService.cancelBooking(id, user.getId()));
    }
}