package com.ridemate.service;

import com.ridemate.document.MessageDocument;
import com.ridemate.dto.response.ConversationDTO;
import com.ridemate.model.User;
import com.ridemate.repository.mongo.MessageMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMongoRepository messageMongoRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageDocument sendMessage(Long senderId, Long receiverId, String content) {
        // Validate users exist
        userService.getUserById(senderId);
        userService.getUserById(receiverId);

        MessageDocument message = MessageDocument.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .build();

        MessageDocument savedMessage = messageMongoRepository.save(message);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                savedMessage
        );

        return savedMessage;
    }

    public List<MessageDocument> getConversation(Long user1Id, Long user2Id) {
        return messageMongoRepository.findConversation(user1Id, user2Id);
    }

    public List<MessageDocument> getUnreadMessages(Long userId) {
        return messageMongoRepository.findByReceiverIdAndIsReadFalse(userId);
    }

    // Groups all messages involving the user into one entry per contact, sorted by recency.
    // Done in Java rather than MongoDB aggregation to keep it readable and avoid pipeline complexity.
    public List<ConversationDTO> getConversations(Long userId) {
        List<MessageDocument> all = messageMongoRepository
                .findBySenderIdOrReceiverIdOrderByCreatedAtDesc(userId, userId);

        // LinkedHashMap preserves insertion order — first entry per key = most recent message
        Map<Long, MessageDocument> lastMessageByContact = new LinkedHashMap<>();
        Map<Long, Integer> unreadByContact = new HashMap<>();

        for (MessageDocument msg : all) {
            Long contactId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            lastMessageByContact.putIfAbsent(contactId, msg);
            if (msg.getReceiverId().equals(userId) && !msg.getIsRead()) {
                unreadByContact.merge(contactId, 1, Integer::sum);
            }
        }

        return lastMessageByContact.entrySet().stream().map(entry -> {
            User contact = userService.getUserById(entry.getKey());
            MessageDocument last = entry.getValue();
            return ConversationDTO.builder()
                    .otherUserId(contact.getId())
                    .firstName(contact.getFirstName())
                    .lastName(contact.getLastName())
                    .profilePicture(contact.getProfilePicture())
                    .lastMessage(last.getContent())
                    .lastMessageTime(last.getCreatedAt())
                    .unreadCount(unreadByContact.getOrDefault(entry.getKey(), 0))
                    .build();
        }).collect(Collectors.toList());
    }

    public MessageDocument markAsRead(String messageId) {
        MessageDocument message = messageMongoRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setIsRead(true);
        return messageMongoRepository.save(message);
    }
}