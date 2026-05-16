package com.ridemate.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDocument {
    @Id
    private String id;

    @Field("sender_id")
    private Long senderId;

    @Field("receiver_id")
    private Long receiverId;

    @Field("content")
    private String content;

    @Builder.Default
    @Field("is_read")
    private Boolean isRead = false;

    @Builder.Default
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
