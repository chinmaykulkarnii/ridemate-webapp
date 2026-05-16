package com.ridemate.controller;

import com.ridemate.dto.request.RideRequest;
import com.ridemate.model.Ride;
import com.ridemate.model.User;
import com.ridemate.model.enums.VehicleType;
import com.ridemate.service.RideService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RideController {

    private final RideService rideService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Ride> createRide(@RequestBody RideRequest request,
                                           Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(rideService.createRide(user.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable Long id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Ride>> getRidesByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Ride>> searchRides(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime,
            @RequestParam(defaultValue = "1") Integer seatsRequired,
            @RequestParam(required = false) VehicleType vehicleType,
            @RequestParam(required = false) Double maxPrice) {

        LocalDateTime searchFrom = (departureTime != null) ? departureTime : LocalDateTime.now();

        if (vehicleType != null || maxPrice != null) {
            return ResponseEntity.ok(rideService.searchRidesWithFilters(
                    origin, destination, searchFrom, seatsRequired, vehicleType, maxPrice));
        }

        return ResponseEntity.ok(rideService.searchRides(
                origin, destination, searchFrom, seatsRequired));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ride> updateRide(@PathVariable Long id,
                                           @RequestBody RideRequest request,
                                           Authentication authentication) {
        Ride ride = rideService.getRideById(id);
        User user = userService.getUserByEmail(authentication.getName());

        if (!ride.getDriver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(rideService.updateRide(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRide(@PathVariable Long id,
                                             Authentication authentication) {
        Ride ride = rideService.getRideById(id);
        User user = userService.getUserByEmail(authentication.getName());

        if (!ride.getDriver().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        rideService.deleteRide(id);
        return ResponseEntity.ok("Ride deleted successfully");
    }
}