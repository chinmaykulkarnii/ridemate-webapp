package com.ridemate.service;

import com.ridemate.document.NotificationDocument;
import com.ridemate.model.EmergencySOS;
import com.ridemate.model.User;
import com.ridemate.model.UserRole;
import com.ridemate.repository.UserRepository;
import com.ridemate.repository.mongo.NotificationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMongoRepository notificationMongoRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationDocument createNotification(Long userId, String title, String message) {
        // Validate user exists
        userService.getUserById(userId);

        NotificationDocument notification = NotificationDocument.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .build();

        NotificationDocument savedNotification = notificationMongoRepository.save(notification);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                savedNotification
        );

        return savedNotification;
    }

    public void notifyAdminsOfEmergency(EmergencySOS sos) {
        // Find all admin users
        List<User> allUsers = userRepository.findAll();
        List<User> admins = allUsers.stream()
                .filter(user -> user.hasRole(UserRole.ADMIN))
                .toList();

        String title = "🚨 Emergency SOS Alert";
        String message = String.format(
                "Emergency SOS triggered by %s %s. Location: %s. Ride ID: %s",
                sos.getUser().getFirstName(),
                sos.getUser().getLastName(),
                sos.getLocation() != null ? sos.getLocation() : "Unknown",
                sos.getRide() != null ? sos.getRide().getId() : "N/A"
        );

        admins.forEach(admin -> createNotification(admin.getId(), title, message));
    }

    public List<NotificationDocument> getUserNotifications(Long userId) {
        return notificationMongoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<NotificationDocument> getUnreadNotifications(Long userId) {
        return notificationMongoRepository.findByUserIdAndIsReadFalse(userId);
    }

    public NotificationDocument markAsRead(String notificationId) {
        NotificationDocument notification = notificationMongoRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        return notificationMongoRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<NotificationDocument> notifications = getUnreadNotifications(userId);
        notifications.forEach(n -> n.setIsRead(true));
        notificationMongoRepository.saveAll(notifications);
    }
}
