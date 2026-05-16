package com.ridemate.service;

import com.ridemate.model.*;
import com.ridemate.repository.PointsHistoryRepository;
import com.ridemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {

    private final UserRepository userRepository;
    private final PointsHistoryRepository pointsHistoryRepository;

    // Points configuration
    private static final int POINTS_RIDE_COMPLETED = 10;
    private static final int POINTS_RIDE_OFFERED = 15;
    private static final int POINTS_REFERRAL = 50;
    private static final int POINTS_RATING_GIVEN = 5;
    private static final int POINTS_PROFILE_COMPLETED = 20;
    private static final int POINTS_VERIFICATION = 30;
    private static final int POINTS_FIRST_RIDE = 25;

    // Badge thresholds
    private static final Map<String, Integer> BADGE_THRESHOLDS = Map.of(
            "BRONZE_RIDER", 5,
            "SILVER_RIDER", 20,
            "GOLD_RIDER", 50,
            "PLATINUM_RIDER", 100,
            "ECO_WARRIOR", 10,  // 10 rides completed
            "VERIFIED_USER", 1,
            "SOCIAL_BUTTERFLY", 5,  // 5 referrals
            "RATING_CHAMPION", 4    // Average rating >= 4.0
    );

    @Transactional
    public void awardPoints(User user, PointsHistory.PointsAction action, int points, String description,
                           Ride ride, Booking booking, Long referredUserId) {
        PointsHistory history = PointsHistory.builder()
                .user(user)
                .points(points)
                .action(action)
                .description(description)
                .relatedRide(ride)
                .relatedBooking(booking)
                .referredUserId(referredUserId)
                .build();

        pointsHistoryRepository.save(history);

        // Update user points
        user.setPoints(user.getPoints() + points);
        userRepository.save(user);

        // Check and award badges
        checkAndAwardBadges(user);

        log.info("Awarded {} points to user {} for action {}", points, user.getId(), action);
    }

    @Transactional
    public void awardPointsForRideCompleted(User user, Ride ride, Booking booking) {
        awardPoints(user, PointsHistory.PointsAction.RIDE_COMPLETED, POINTS_RIDE_COMPLETED,
                "Ride completed", ride, booking, null);
    }

    @Transactional
    public void awardPointsForRideOffered(User driver, Ride ride) {
        awardPoints(driver, PointsHistory.PointsAction.RIDE_OFFERED, POINTS_RIDE_OFFERED,
                "Ride offered", ride, null, null);
    }

    @Transactional
    public void awardPointsForReferral(User referrer, User referred) {
        awardPoints(referrer, PointsHistory.PointsAction.REFERRAL, POINTS_REFERRAL,
                "Referred user: " + referred.getEmail(), null, null, referred.getId());
    }

    @Transactional
    public void awardPointsForRating(User user, Ride ride, Booking booking) {
        awardPoints(user, PointsHistory.PointsAction.RATING_GIVEN, POINTS_RATING_GIVEN,
                "Rating given", ride, booking, null);
    }

    @Transactional
    public void awardPointsForProfileCompletion(User user) {
        awardPoints(user, PointsHistory.PointsAction.PROFILE_COMPLETED, POINTS_PROFILE_COMPLETED,
                "Profile completed", null, null, null);
    }

    @Transactional
    public void awardPointsForVerification(User user) {
        awardPoints(user, PointsHistory.PointsAction.VERIFICATION, POINTS_VERIFICATION,
                "Profile verified", null, null, null);
        awardBadge(user, "VERIFIED_USER");
    }

    @Transactional
    public void awardPointsForFirstRide(User user, Ride ride, Booking booking) {
        awardPoints(user, PointsHistory.PointsAction.FIRST_RIDE, POINTS_FIRST_RIDE,
                "First ride!", ride, booking, null);
    }

    @Transactional
    public void checkAndAwardBadges(User user) {
        if (user.getBadges() == null) {
            user.setBadges(new HashSet<>());
        }

        // Ride count badges
        int totalRides = user.getTotalRidesOffered() + user.getTotalRidesTaken();

        if (totalRides >= 5 && !user.getBadges().contains("BRONZE_RIDER")) {
            awardBadge(user, "BRONZE_RIDER");
        }
        if (totalRides >= 20 && !user.getBadges().contains("SILVER_RIDER")) {
            awardBadge(user, "SILVER_RIDER");
        }
        if (totalRides >= 50 && !user.getBadges().contains("GOLD_RIDER")) {
            awardBadge(user, "GOLD_RIDER");
        }
        if (totalRides >= 100 && !user.getBadges().contains("PLATINUM_RIDER")) {
            awardBadge(user, "PLATINUM_RIDER");
        }

        // Eco warrior badge
        if (totalRides >= 10 && !user.getBadges().contains("ECO_WARRIOR")) {
            awardBadge(user, "ECO_WARRIOR");
        }

        // Rating champion badge
        if (user.getAverageRating() >= 4.0 && user.getTotalRatings() >= 5
                && !user.getBadges().contains("RATING_CHAMPION")) {
            awardBadge(user, "RATING_CHAMPION");
        }

        userRepository.save(user);
    }

    @Transactional
    public void awardBadge(User user, String badgeName) {
        if (user.getBadges() == null) {
            user.setBadges(new HashSet<>());
        }

        if (user.getBadges().add(badgeName)) {
            userRepository.save(user);
            log.info("Awarded badge {} to user {}", badgeName, user.getId());
        }
    }

    public List<PointsHistory> getUserPointsHistory(Long userId) {
        return pointsHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Integer getUserTotalPoints(Long userId) {
        Integer points = pointsHistoryRepository.getTotalPointsByUserId(userId);
        return points != null ? points : 0;
    }

    /**
     * Generate referral code for user
     */
    public String generateReferralCode(User user) {
        String code = "REF" + user.getId() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setReferralCode(code);
        userRepository.save(user);
        return code;
    }

    /**
     * Apply referral code when user signs up
     */
    @Transactional
    public boolean applyReferralCode(User newUser, String referralCode) {
        Optional<User> referrerOpt = userRepository.findByReferralCode(referralCode);
        if (referrerOpt.isPresent() && !referrerOpt.get().getId().equals(newUser.getId())) {
            User referrer = referrerOpt.get();
            newUser.setReferredBy(referrer.getId());
            userRepository.save(newUser);

            // Award points to referrer
            awardPointsForReferral(referrer, newUser);

            // Award bonus points to new user
            awardPoints(newUser, PointsHistory.PointsAction.REFERRAL, 25,
                    "Signed up with referral code", null, null, referrer.getId());

            return true;
        }
        return false;
    }

    /**
     * Redeem points (for discounts, etc.)
     */
    @Transactional
    public boolean redeemPoints(User user, int pointsToRedeem, String description) {
        if (user.getPoints() >= pointsToRedeem) {
            awardPoints(user, PointsHistory.PointsAction.REDEEMED, -pointsToRedeem,
                    description, null, null, null);
            return true;
        }
        return false;
    }
}
