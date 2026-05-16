package com.ridemate.repository;

import com.ridemate.model.PointsHistory;
import com.ridemate.model.PointsHistory.PointsAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointsHistoryRepository extends JpaRepository<PointsHistory, Long> {
    List<PointsHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PointsHistory> findByUserIdAndAction(Long userId, PointsAction action);

    @Query("SELECT SUM(p.points) FROM PointsHistory p WHERE p.user.id = :userId")
    Integer getTotalPointsByUserId(Long userId);
}
