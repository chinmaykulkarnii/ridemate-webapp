package com.ridemate.service;

import com.ridemate.model.Booking;
import com.ridemate.model.EmergencySOS;
import com.ridemate.model.Ride;
import com.ridemate.model.User;
import com.ridemate.repository.EmergencySOSRepository;
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
public class EmergencyService {

    private final EmergencySOSRepository emergencySOSRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final LocationService locationService;

    /**
     * Trigger SOS alert
     */
    @Transactional
    public EmergencySOS triggerSOS(User user, Ride ride, Booking booking, Double lat, Double lng, String notes) {
        // Reverse geocode location if coordinates provided
        String location = null;
        if (lat != null && lng != null) {
            location = locationService.reverseGeocode(lat, lng);
        }

        EmergencySOS sos = EmergencySOS.builder()
                .user(user)
                .ride(ride)
                .booking(booking)
                .latitude(lat)
                .longitude(lng)
                .location(location)
                .notes(notes)
                .status(EmergencySOS.SOSStatus.ACTIVE)
                .build();

        sos = emergencySOSRepository.save(sos);

        // Send emergency notification to admins
        notificationService.notifyAdminsOfEmergency(sos);

        // If in a ride, notify the driver/passengers
        if (ride != null) {
            notifyRideParticipants(sos, ride, user);
        }

        // Broadcast SOS to admin dashboard via WebSocket
        messagingTemplate.convertAndSend("/topic/admin/emergency", sos);

        log.warn("SOS triggered by user {} for ride {}", user.getId(), ride != null ? ride.getId() : "N/A");

        return sos;
    }

    /**
     * Notify driver and passengers about SOS
     */
    private void notifyRideParticipants(EmergencySOS sos, Ride ride, User requester) {
        String message = String.format("Emergency SOS triggered by %s %s",
                requester.getFirstName(), requester.getLastName());

        // Notify driver
        if (!ride.getDriver().getId().equals(requester.getId())) {
            notificationService.createNotification(
                    ride.getDriver().getId(),
                    "Emergency Alert",
                    message
            );
        }

        // Notify passengers (if any bookings)
        if (ride.getBookings() != null) {
            ride.getBookings().forEach(booking -> {
                if (!booking.getPassenger().getId().equals(requester.getId())) {
                    notificationService.createNotification(
                            booking.getPassenger().getId(),
                            "Emergency Alert",
                            message
                    );
                }
            });
        }
    }

    /**
     * Resolve SOS
     */
    @Transactional
    public EmergencySOS resolveSOS(Long sosId, String resolvedBy) {
        EmergencySOS sos = emergencySOSRepository.findById(sosId)
                .orElseThrow(() -> new RuntimeException("SOS not found"));

        sos.setStatus(EmergencySOS.SOSStatus.RESOLVED);
        sos.setResolvedAt(LocalDateTime.now());
        sos.setResolvedBy(resolvedBy);

        sos = emergencySOSRepository.save(sos);

        // Notify the user who triggered SOS
        notificationService.createNotification(
                sos.getUser().getId(),
                "SOS Resolved",
                "Your emergency alert has been resolved."
        );

        log.info("SOS {} resolved by {}", sosId, resolvedBy);

        return sos;
    }

    /**
     * Mark SOS as false alarm
     */
    @Transactional
    public EmergencySOS markAsFalseAlarm(Long sosId, Long userId) {
        EmergencySOS sos = emergencySOSRepository.findById(sosId)
                .orElseThrow(() -> new RuntimeException("SOS not found"));

        // Verify user is the one who triggered SOS
        if (!sos.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        sos.setStatus(EmergencySOS.SOSStatus.FALSE_ALARM);
        sos.setResolvedAt(LocalDateTime.now());

        return emergencySOSRepository.save(sos);
    }

    /**
     * Get all active SOS alerts
     */
    public List<EmergencySOS> getActiveSOS() {
        return emergencySOSRepository.findByStatusOrderByCreatedAtDesc(EmergencySOS.SOSStatus.ACTIVE);
    }

    /**
     * Get user's SOS history
     */
    public List<EmergencySOS> getUserSOSHistory(Long userId) {
        return emergencySOSRepository.findByUserId(userId);
    }

    /**
     * Get SOS alerts for a ride
     */
    public List<EmergencySOS> getRideSOSAlerts(Long rideId) {
        return emergencySOSRepository.findByRideId(rideId);
    }

    /**
     * Get SOS by ID
     */
    public EmergencySOS getSOSById(Long sosId) {
        return emergencySOSRepository.findById(sosId)
                .orElseThrow(() -> new RuntimeException("SOS not found"));
    }

    /**
     * Share trip details with emergency contact
     */
    public String generateEmergencyShareLink(Long rideId, Long userId) {
        // Generate a shareable emergency link with ride details
        // In production, this would be a secure token-based link
        return "/emergency/track/" + rideId + "/" + userId;
    }
}
