package com.example.demo.repository;

import com.example.demo.entity.Contradiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContradictionRepository extends JpaRepository<Contradiction, Long> {
    
    List<Contradiction> findByHabitId(Long habitId);
    
    List<Contradiction> findByContradictionType(Contradiction.ContradictionType type);
    
    List<Contradiction> findBySeverity(Contradiction.Severity severity);
    
    @Query("SELECT c FROM Contradiction c ORDER BY c.detectedAt DESC")
    List<Contradiction> findAllOrderByDetectedAtDesc();
    
    @Query("SELECT c FROM Contradiction c WHERE c.habitId = ?1 ORDER BY c.detectedAt DESC")
    List<Contradiction> findByHabitIdOrderByDetectedAtDesc(Long habitId);
}
