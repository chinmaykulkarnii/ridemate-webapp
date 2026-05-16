package com.ridemate.service;

import com.ridemate.model.Booking;
import com.ridemate.model.Ride;
import com.ridemate.model.User;
import com.ridemate.repository.BookingRepository;
import com.ridemate.repository.PaymentRepository;
import com.ridemate.repository.RideRepository;
import com.ridemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    // Carbon emission factor: average 120g CO2 per km per passenger for cars
    private static final double CO2_PER_KM_PER_PASSENGER = 0.12; // kg

    /**
     * Calculate carbon footprint saved by carpooling
     */
    public double calculateCarbonSaved(Ride ride, int passengers) {
        if (ride.getDistance() == null) {
            return 0.0;
        }

        // Carbon saved = distance * passengers * CO2 factor
        // Carpooling saves emissions by reducing individual trips
        return ride.getDistance() * passengers * CO2_PER_KM_PER_PASSENGER;
    }

    /**
     * Update user's carbon savings after ride completion
     */
    public void updateUserCarbonSavings(User user, double carbonSaved) {
        user.setTotalCarbonSaved(user.getTotalCarbonSaved() + carbonSaved);
        userRepository.save(user);
    }

    /**
     * Get user analytics
     */
    public Map<String, Object> getUserAnalytics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalRidesOffered", user.getTotalRidesOffered());
        analytics.put("totalRidesTaken", user.getTotalRidesTaken());
        analytics.put("totalDistanceTraveled", user.getTotalDistanceTraveled());
        analytics.put("totalCarbonSaved", user.getTotalCarbonSaved());
        analytics.put("averageRating", user.getAverageRating());
        analytics.put("totalRatings", user.getTotalRatings());
        analytics.put("points", user.getPoints());
        analytics.put("badges", user.getBadges());

        // Calculate cost savings (assuming average taxi cost of 15 per km)
        double costSavings = user.getTotalDistanceTraveled() * 10; // Simplified calculation
        analytics.put("estimatedCostSavings", costSavings);

        // Environmental impact equivalent
        analytics.put("treesEquivalent", user.getTotalCarbonSaved() / 20); // 1 tree absorbs ~20kg CO2/year

        return analytics;
    }

    /**
     * Get admin dashboard analytics
     */
    public Map<String, Object> getAdminAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // User statistics
        long totalUsers = userRepository.count();
        analytics.put("totalUsers", totalUsers);

        // Ride statistics
        long totalRides = rideRepository.count();
        List<Ride> activeRides = rideRepository.findByIsActive(true);
        analytics.put("totalRides", totalRides);
        analytics.put("activeRides", activeRides.size());

        // Booking statistics
        long totalBookings = bookingRepository.count();
        analytics.put("totalBookings", totalBookings);

        // Calculate total distance and carbon saved
        List<Ride> allRides = rideRepository.findAll();
        double totalDistance = allRides.stream()
                .filter(ride -> ride.getDistance() != null)
                .mapToDouble(Ride::getDistance)
                .sum();

        double totalCarbon = allRides.stream()
                .filter(ride -> ride.getDistance() != null)
                .mapToDouble(ride -> {
                    int passengers = ride.getTotalSeats() - ride.getAvailableSeats();
                    return calculateCarbonSaved(ride, passengers);
                })
                .sum();

        analytics.put("totalDistanceTraveled", totalDistance);
        analytics.put("totalCarbonSaved", totalCarbon);

        // Recent activity (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentUsers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);
        analytics.put("newUsersLast30Days", recentUsers);

        // Payment statistics
        analytics.put("totalTransactions", paymentRepository.count());

        // Top drivers by rides offered
        analytics.put("topDrivers", getTopDrivers(10));

        // Verification statistics
        long verifiedUsers = userRepository.countByIsIdVerifiedTrue();
        analytics.put("verifiedUsers", verifiedUsers);

        return analytics;
    }

    /**
     * Get top drivers
     */
    public List<Map<String, Object>> getTopDrivers(int limit) {
        // This is a simplified version. In production, you'd use a custom query
        List<User> users = userRepository.findAll();

        return users.stream()
                .sorted((u1, u2) -> Integer.compare(u2.getTotalRidesOffered(), u1.getTotalRidesOffered()))
                .limit(limit)
                .map(user -> {
                    Map<String, Object> driverInfo = new HashMap<>();
                    driverInfo.put("id", user.getId());
                    driverInfo.put("name", user.getFirstName() + " " + user.getLastName());
                    driverInfo.put("totalRidesOffered", user.getTotalRidesOffered());
                    driverInfo.put("averageRating", user.getAverageRating());
                    return driverInfo;
                })
                .toList();
    }

    /**
     * Get ride analytics for a specific ride
     */
    public Map<String, Object> getRideAnalytics(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        Map<String, Object> analytics = new HashMap<>();

        analytics.put("rideId", ride.getId());
        analytics.put("distance", ride.getDistance());
        analytics.put("duration", ride.getDuration());
        analytics.put("totalSeats", ride.getTotalSeats());
        analytics.put("bookedSeats", ride.getTotalSeats() - ride.getAvailableSeats());
        analytics.put("availableSeats", ride.getAvailableSeats());

        // Calculate earnings for driver
        int bookedSeats = ride.getTotalSeats() - ride.getAvailableSeats();
        double earnings = bookedSeats * ride.getFinalPricePerSeat();
        analytics.put("estimatedEarnings", earnings);

        // Carbon saved
        double carbonSaved = calculateCarbonSaved(ride, bookedSeats);
        analytics.put("carbonSaved", carbonSaved);

        return analytics;
    }

    /**
     * Calculate price surge factor based on demand
     */
    public double calculateSurgeFactor(String origin, String destination, LocalDateTime departureTime) {
        // Find similar rides in the same time window
        LocalDateTime windowStart = departureTime.minusHours(1);
        LocalDateTime windowEnd = departureTime.plusHours(1);

        // This is a simplified algorithm. In production, you'd use more sophisticated logic
        long ridesInWindow = rideRepository.countByOriginAndDestinationAndDepartureTimeBetween(
                origin, destination, windowStart, windowEnd);

        // If many people are searching for same route, increase surge
        if (ridesInWindow > 10) {
            return 1.5;
        } else if (ridesInWindow > 5) {
            return 1.25;
        }

        return 1.0;
    }
}
