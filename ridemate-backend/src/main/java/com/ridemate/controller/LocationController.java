package com.ridemate.controller;

import com.ridemate.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/geocode")
    public ResponseEntity<LocationService.GeocodeResult> geocode(@RequestParam String address) {
        LocationService.GeocodeResult result = locationService.geocode(address);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/reverse-geocode")
    public ResponseEntity<String> reverseGeocode(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        String address = locationService.reverseGeocode(lat, lng);
        if (address != null) {
            return ResponseEntity.ok(address);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/route")
    public ResponseEntity<LocationService.RouteInfo> getRoute(
            @RequestParam Double startLat,
            @RequestParam Double startLng,
            @RequestParam Double endLat,
            @RequestParam Double endLng) {
        LocationService.RouteInfo routeInfo = locationService.getRouteInfo(startLat, startLng, endLat, endLng);
        if (routeInfo != null) {
            return ResponseEntity.ok(routeInfo);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationService.PlaceSuggestion>> searchPlaces(@RequestParam String query) {
        List<LocationService.PlaceSuggestion> suggestions = locationService.searchPlaces(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/distance")
    public ResponseEntity<Double> calculateDistance(
            @RequestParam Double lat1,
            @RequestParam Double lng1,
            @RequestParam Double lat2,
            @RequestParam Double lng2) {
        double distance = locationService.calculateHaversineDistance(lat1, lng1, lat2, lng2);
        return ResponseEntity.ok(distance);
    }
}
