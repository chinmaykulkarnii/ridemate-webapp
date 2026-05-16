package com.ridemate.service;

import com.ridemate.model.Ride;
import com.ridemate.model.RideLocationDocument;
import com.ridemate.repository.RideRepository;
import com.ridemate.repository.mongo.RideLocationMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideTrackingService {

    private final RideRepository rideRepository;
    private final RideLocationMongoRepository rideLocationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LocationService locationService;

    /**
     * Update driver's current location for a ride
     */
    @Transactional
    public void updateRideLocation(Long rideId, Long driverId, Double lat, Double lng, Double speed, Double heading) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Verify that the driver is the owner of the ride
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Unauthorized to update location");
        }

        // Update ride's current location
        ride.setCurrentLat(lat);
        ride.setCurrentLng(lng);
        ride.setLastLocationUpdate(LocalDateTime.now());
        rideRepository.save(ride);

        // Calculate ETA if passengers are waiting
        Integer etaMinutes = null;
        Double distanceKm = null;

        if (ride.getOriginLat() != null && ride.getOriginLng() != null) {
            LocationService.RouteInfo routeInfo = locationService.getRouteInfo(lat, lng,
                    ride.getOriginLat(), ride.getOriginLng());
            if (routeInfo != null) {
                etaMinutes = routeInfo.duration();
                distanceKm = routeInfo.distance();
            }
        }

        // Save location history to MongoDB
        RideLocationDocument locationDoc = RideLocationDocument.builder()
                .rideId(rideId)
                .driverId(driverId)
                .latitude(lat)
                .longitude(lng)
                .speed(speed)
                .heading(heading)
                .estimatedTimeToPickup(etaMinutes)
                .distanceToPickup(distanceKm)
                .build();

        rideLocationRepository.save(locationDoc);

        // Broadcast location update to all passengers via WebSocket
        messagingTemplate.convertAndSend("/topic/ride/" + rideId + "/location", locationDoc);

        log.debug("Updated location for ride {}: {}, {}", rideId, lat, lng);
    }

    /**
     * Get current location of a ride
     */
    public RideLocationDocument getCurrentRideLocation(Long rideId) {
        return rideLocationRepository.findTopByRideIdOrderByTimestampDesc(rideId)
                .orElse(null);
    }

    /**
     * Get location history for a ride
     */
    public List<RideLocationDocument> getRideLocationHistory(Long rideId) {
        return rideLocationRepository.findByRideIdOrderByTimestampDesc(rideId);
    }

    /**
     * Get recent location updates (within last N minutes)
     */
    public List<RideLocationDocument> getRecentLocationUpdates(Long rideId, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return rideLocationRepository.findByRideIdAndTimestampAfter(rideId, since);
    }

    /**
     * Enable tracking for a ride
     */
    @Transactional
    public void enableTracking(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Unauthorized");
        }

        ride.setTrackingEnabled(true);
        rideRepository.save(ride);

        log.info("Enabled tracking for ride {}", rideId);
    }

    /**
     * Disable tracking for a ride
     */
    @Transactional
    public void disableTracking(Long rideId, Long driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Unauthorized");
        }

        ride.setTrackingEnabled(false);
        ride.setCurrentLat(null);
        ride.setCurrentLng(null);
        ride.setLastLocationUpdate(null);
        rideRepository.save(ride);

        // Clear location history
        rideLocationRepository.deleteByRideId(rideId);

        log.info("Disabled tracking for ride {}", rideId);
    }

    /**
     * Check if driver is approaching pickup location
     */
    public boolean isDriverApproaching(Long rideId, double thresholdKm) {
        RideLocationDocument currentLocation = getCurrentRideLocation(rideId);

        if (currentLocation != null && currentLocation.getDistanceToPickup() != null) {
            return currentLocation.getDistanceToPickup() <= thresholdKm;
        }

        return false;
    }

    /**
     * Generate shareable tracking link
     */
    public String generateTrackingLink(Long rideId) {
        // In production, this would generate a secure token-based link
        return "/track-ride/" + rideId;
    }
}
