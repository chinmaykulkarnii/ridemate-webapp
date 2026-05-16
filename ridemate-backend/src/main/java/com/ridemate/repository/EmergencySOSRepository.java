package com.ridemate.repository;

import com.ridemate.model.EmergencySOS;
import com.ridemate.model.EmergencySOS.SOSStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencySOSRepository extends JpaRepository<EmergencySOS, Long> {
    List<EmergencySOS> findByUserId(Long userId);
    List<EmergencySOS> findByRideId(Long rideId);
    List<EmergencySOS> findByStatus(SOSStatus status);
    List<EmergencySOS> findByStatusOrderByCreatedAtDesc(SOSStatus status);
}
