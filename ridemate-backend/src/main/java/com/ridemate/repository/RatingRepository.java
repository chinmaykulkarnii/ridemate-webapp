package com.ridemate.repository;

import com.ridemate.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRateeId(Long rateeId);
    List<Rating> findByBookingId(Long bookingId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.ratee.id = :userId")
    Double getAverageRatingForUser(Long userId);
}