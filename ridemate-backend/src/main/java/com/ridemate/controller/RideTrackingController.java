package com.ridemate.controller;

import com.ridemate.model.RideLocationDocument;
import com.ridemate.service.RideTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class RideTrackingController {

    private final RideTrackingService rideTrackingService;

    @PostMapping("/rides/{rideId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long rideId,
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = Long.parseLong(userDetails.getUsername());
        Double lat = Double.parseDouble(request.get("latitude").toString());
        Double lng = Double.parseDouble(request.get("longitude").toString());
        Double speed = request.get("speed") != null ?
                Double.parseDouble(request.get("speed").toString()) : null;
        Double heading = request.get("heading") != null ?
                Double.parseDouble(request.get("heading").toString()) : null;

        rideTrackingService.updateRideLocation(rideId, driverId, lat, lng, speed, heading);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rides/{rideId}/current-location")
    public ResponseEntity<RideLocationDocument> getCurrentLocation(@PathVariable Long rideId) {
        RideLocationDocument location = rideTrackingService.getCurrentRideLocation(rideId);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rides/{rideId}/location-history")
    public ResponseEntity<List<RideLocationDocument>> getLocationHistory(@PathVariable Long rideId) {
        List<RideLocationDocument> history = rideTrackingService.getRideLocationHistory(rideId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/rides/{rideId}/enable")
    public ResponseEntity<Void> enableTracking(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = Long.parseLong(userDetails.getUsername());
        rideTrackingService.enableTracking(rideId, driverId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rides/{rideId}/disable")
    public ResponseEntity<Void> disableTracking(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long driverId = Long.parseLong(userDetails.getUsername());
        rideTrackingService.disableTracking(rideId, driverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rides/{rideId}/share-link")
    public ResponseEntity<Map<String, String>> getShareLink(@PathVariable Long rideId) {
        String link = rideTrackingService.generateTrackingLink(rideId);
        return ResponseEntity.ok(Map.of("trackingLink", link));
    }
}
