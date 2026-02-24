package com.example.demo.repository;

import com.example.demo.entity.HabitDeclaration;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitDeclarationRepository extends JpaRepository<HabitDeclaration, Long> {
    
    List<HabitDeclaration> findByIsActiveTrue();
    
    List<HabitDeclaration> findByFrequency(HabitDeclaration.Frequency frequency);
    
    List<HabitDeclaration> findByUser(User user);
    
    List<HabitDeclaration> findByUserId(Long userId);
    
    Optional<HabitDeclaration> findByIdAndUserId(Long id, Long userId);
}
