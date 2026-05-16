package com.ridemate.controller;

import com.ridemate.model.PointsHistory;
import com.ridemate.service.GamificationService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;
    private final UserService userService;

    @GetMapping("/points/history")
    public ResponseEntity<List<PointsHistory>> getPointsHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<PointsHistory> history = gamificationService.getUserPointsHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/points/total")
    public ResponseEntity<Integer> getTotalPoints(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Integer points = gamificationService.getUserTotalPoints(userId);
        return ResponseEntity.ok(points);
    }

    @PostMapping("/referral/generate")
    public ResponseEntity<Map<String, String>> generateReferralCode(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        var user = userService.getUserById(userId);
        String code = gamificationService.generateReferralCode(user);

        Map<String, String> response = new HashMap<>();
        response.put("referralCode", code);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/referral/apply")
    public ResponseEntity<Map<String, Object>> applyReferralCode(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        var user = userService.getUserById(userId);
        String code = request.get("code");

        boolean success = gamificationService.applyReferralCode(user, code);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemPoints(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        var user = userService.getUserById(userId);
        int points = (Integer) request.get("points");
        String description = (String) request.get("description");

        boolean success = gamificationService.redeemPoints(user, points, description);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }
}
