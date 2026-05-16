package com.ridemate.service;

import com.ridemate.dto.request.RatingRequest;
import com.ridemate.model.Booking;
import com.ridemate.model.Rating;
import com.ridemate.model.User;
import com.ridemate.model.enums.RatingParameter;
import com.ridemate.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final BookingService bookingService;
    private final UserService userService;

    @Transactional
    public List<Rating> createRatings(Long raterId, RatingRequest request) {
        User rater = userService.getUserById(raterId);
        User ratee = userService.getUserById(request.getRateeId());
        Booking booking = bookingService.getBookingById(request.getBookingId());

        // Validate that rater is part of the booking
        boolean isDriver = booking.getRide().getDriver().getId().equals(raterId);
        boolean isPassenger = booking.getPassenger().getId().equals(raterId);

        if (!isDriver && !isPassenger) {
            throw new RuntimeException("You are not part of this booking");
        }

        List<Rating> ratings = new ArrayList<>();

        for (Map.Entry<RatingParameter, Integer> entry : request.getRatings().entrySet()) {
            if (entry.getValue() < 1 || entry.getValue() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }

            Rating rating = Rating.builder()
                    .booking(booking)
                    .rater(rater)
                    .ratee(ratee)
                    .parameter(entry.getKey())
                    .stars(entry.getValue())
                    .build();

            ratings.add(ratingRepository.save(rating));
        }

        // Update user's average rating
        updateUserAverageRating(request.getRateeId());

        return ratings;
    }

    private void updateUserAverageRating(Long userId) {
        Double avgRating = ratingRepository.getAverageRatingForUser(userId);
        User user = userService.getUserById(userId);

        if (avgRating != null) {
            user.setAverageRating(avgRating);
            user.setTotalRatings(ratingRepository.findByRateeId(userId).size());
            userService.updateUserRating(userId);
        }
    }

    public List<Rating> getRatingsByUser(Long userId) {
        return ratingRepository.findByRateeId(userId);
    }

    public List<Rating> getRatingsByBooking(Long bookingId) {
        return ratingRepository.findByBookingId(bookingId);
    }
}