package com.ridemate.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDocument {
    @Id
    private String id;

    @Field("user_id")
    private Long userId;

    @Field("title")
    private String title;

    @Field("message")
    private String message;

    @Builder.Default
    @Field("is_read")
    private Boolean isRead = false;

    @Builder.Default
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
