package com.ridemate.repository.mongo;

import com.ridemate.document.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMongoRepository extends MongoRepository<MessageDocument, String> {

    @Query("{ $or: [ " +
            "{ $and: [ { 'sender_id': ?0 }, { 'receiver_id': ?1 } ] }, " +
            "{ $and: [ { 'sender_id': ?1 }, { 'receiver_id': ?0 } ] } " +
            "] }")
    List<MessageDocument> findConversation(Long user1Id, Long user2Id);

    List<MessageDocument> findByReceiverIdAndIsReadFalse(Long receiverId);

    List<MessageDocument> findBySenderIdOrReceiverIdOrderByCreatedAtDesc(Long senderId, Long receiverId);
}
