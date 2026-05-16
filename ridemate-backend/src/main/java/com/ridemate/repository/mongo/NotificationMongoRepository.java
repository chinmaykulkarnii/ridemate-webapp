package com.ridemate.repository.mongo;

import com.ridemate.document.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationMongoRepository extends MongoRepository<NotificationDocument, String> {

    List<NotificationDocument> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NotificationDocument> findByUserIdAndIsReadFalse(Long userId);
}
