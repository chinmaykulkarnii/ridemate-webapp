package com.ridemate.repository.mongo;

import com.ridemate.model.RideLocationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideLocationMongoRepository extends MongoRepository<RideLocationDocument, String> {
    List<RideLocationDocument> findByRideIdOrderByTimestampDesc(Long rideId);
    Optional<RideLocationDocument> findTopByRideIdOrderByTimestampDesc(Long rideId);
    List<RideLocationDocument> findByDriverIdOrderByTimestampDesc(Long driverId);
    List<RideLocationDocument> findByRideIdAndTimestampAfter(Long rideId, LocalDateTime timestamp);
    void deleteByRideId(Long rideId);
}
