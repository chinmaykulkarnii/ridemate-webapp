package com.ridemate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long otherUserId;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    // Count of messages sent TO the current user that are unread
    private int unreadCount;
}
