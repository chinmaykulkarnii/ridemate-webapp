package com.ridemate.controller;

import com.ridemate.model.EmergencySOS;
import com.ridemate.model.User;
import com.ridemate.service.BookingService;
import com.ridemate.service.EmergencyService;
import com.ridemate.service.RideService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final UserService userService;
    private final RideService rideService;
    private final BookingService bookingService;

    @PostMapping("/sos")
    public ResponseEntity<EmergencySOS> triggerSOS(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        User user = userService.getUserById(userId);

        Long rideId = request.get("rideId") != null ?
                Long.parseLong(request.get("rideId").toString()) : null;
        Long bookingId = request.get("bookingId") != null ?
                Long.parseLong(request.get("bookingId").toString()) : null;

        Double lat = request.get("latitude") != null ?
                Double.parseDouble(request.get("latitude").toString()) : null;
        Double lng = request.get("longitude") != null ?
                Double.parseDouble(request.get("longitude").toString()) : null;

        String notes = (String) request.get("notes");

        var ride = rideId != null ? rideService.getRideById(rideId) : null;
        var booking = bookingId != null ? bookingService.getBookingById(bookingId) : null;

        EmergencySOS sos = emergencyService.triggerSOS(user, ride, booking, lat, lng, notes);
        return ResponseEntity.ok(sos);
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<EmergencySOS> resolveSOS(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String resolvedBy = userDetails.getUsername();
        EmergencySOS sos = emergencyService.resolveSOS(id, resolvedBy);
        return ResponseEntity.ok(sos);
    }

    @PostMapping("/{id}/false-alarm")
    public ResponseEntity<EmergencySOS> markAsFalseAlarm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        EmergencySOS sos = emergencyService.markAsFalseAlarm(id, userId);
        return ResponseEntity.ok(sos);
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmergencySOS>> getActiveSOS() {
        List<EmergencySOS> sosList = emergencyService.getActiveSOS();
        return ResponseEntity.ok(sosList);
    }

    @GetMapping("/history")
    public ResponseEntity<List<EmergencySOS>> getMySOSHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<EmergencySOS> history = emergencyService.getUserSOSHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencySOS> getSOSById(@PathVariable Long id) {
        EmergencySOS sos = emergencyService.getSOSById(id);
        return ResponseEntity.ok(sos);
    }
}
