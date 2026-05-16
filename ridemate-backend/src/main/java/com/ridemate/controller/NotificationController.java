package com.ridemate.controller;

import com.ridemate.document.NotificationDocument;
import com.ridemate.model.User;
import com.ridemate.service.NotificationService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationDocument>> getUserNotifications(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getId()));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDocument>> getUnreadNotifications(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(notificationService.getUnreadNotifications(user.getId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDocument> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok("All notifications marked as read");
    }
}