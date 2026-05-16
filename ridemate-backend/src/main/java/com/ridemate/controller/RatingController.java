package com.ridemate.controller;

import com.ridemate.dto.request.RatingRequest;
import com.ridemate.model.Rating;
import com.ridemate.model.User;
import com.ridemate.service.RatingService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RatingController {

    private final RatingService ratingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<List<Rating>> createRatings(@RequestBody RatingRequest request,
                                                      Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(ratingService.createRatings(user.getId(), request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsByUser(userId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Rating>> getRatingsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ratingService.getRatingsByBooking(bookingId));
    }
}