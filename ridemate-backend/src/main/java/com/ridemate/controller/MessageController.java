package com.ridemate.controller;

import com.ridemate.document.MessageDocument;
import com.ridemate.dto.response.ConversationDTO;
import com.ridemate.model.User;
import com.ridemate.service.MessageService;
import com.ridemate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> message) {
        Long senderId = Long.valueOf(message.get("senderId").toString());
        Long receiverId = Long.valueOf(message.get("receiverId").toString());
        String content = message.get("content").toString();

        messageService.sendMessage(senderId, receiverId, content);
    }

    @GetMapping("/api/messages/conversations")
    @ResponseBody
    public ResponseEntity<List<ConversationDTO>> getConversations(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(messageService.getConversations(user.getId()));
    }

    @GetMapping("/api/messages/conversation/{userId}")
    @ResponseBody
    public ResponseEntity<List<MessageDocument>> getConversation(@PathVariable Long userId,
                                                         Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(messageService.getConversation(user.getId(), userId));
    }

    @GetMapping("/api/messages/unread")
    @ResponseBody
    public ResponseEntity<List<MessageDocument>> getUnreadMessages(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(messageService.getUnreadMessages(user.getId()));
    }

    @PutMapping("/api/messages/{id}/read")
    @ResponseBody
    public ResponseEntity<MessageDocument> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }
}