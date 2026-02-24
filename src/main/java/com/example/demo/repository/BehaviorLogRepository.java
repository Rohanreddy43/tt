package com.example.demo.repository;

import com.example.demo.entity.BehaviorLog;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BehaviorLogRepository extends JpaRepository<BehaviorLog, Long> {
    
    List<BehaviorLog> findByHabitId(Long habitId);
    
    List<BehaviorLog> findByHabitIdAndTimestampBetween(Long habitId, LocalDateTime start, LocalDateTime end);
    
    List<BehaviorLog> findByActionType(BehaviorLog.ActionType actionType);
    
    @Query("SELECT b FROM BehaviorLog b WHERE b.habitId = ?1 ORDER BY b.timestamp DESC")
    List<BehaviorLog> findRecentByHabitId(Long habitId);
    
    List<BehaviorLog> findByUser(User user);
    
    List<BehaviorLog> findByUserId(Long userId);
    
    List<BehaviorLog> findByUserIdAndHabitId(Long userId, Long habitId);
    
    @Query("SELECT b FROM BehaviorLog b WHERE b.user.id = ?1 AND b.habitId = ?2 ORDER BY b.timestamp DESC")
    List<BehaviorLog> findRecentByUserIdAndHabitId(Long userId, Long habitId);
}
