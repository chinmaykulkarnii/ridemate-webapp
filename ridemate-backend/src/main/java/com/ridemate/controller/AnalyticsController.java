package com.ridemate.controller;

import com.ridemate.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserAnalytics(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Map<String, Object> analytics = analyticsService.getUserAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAnalyticsById(@PathVariable Long userId) {
        Map<String, Object> analytics = analyticsService.getUserAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<Map<String, Object>> getRideAnalytics(@PathVariable Long rideId) {
        Map<String, Object> analytics = analyticsService.getRideAnalytics(rideId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/admin/dashboard")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        // In production, add role-based access control here
        Map<String, Object> analytics = analyticsService.getAdminAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
