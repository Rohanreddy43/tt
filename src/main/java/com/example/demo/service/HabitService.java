package com.example.demo.service;

import com.example.demo.dto.HabitDeclarationRequest;
import com.example.demo.dto.HabitDeclarationResponse;
import com.example.demo.entity.HabitDeclaration;
import com.example.demo.entity.User;
import com.example.demo.repository.HabitDeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitDeclarationRepository habitRepository;

    @Transactional
    public HabitDeclarationResponse createHabit(HabitDeclarationRequest request, User user) {
        HabitDeclaration habit = new HabitDeclaration();
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setFrequency(request.getFrequency());
        habit.setExpectedBehavior(request.getExpectedBehavior());
        habit.setConflictingBehavior(request.getConflictingBehavior());
        habit.setIsActive(true);
        habit.setUser(user);
        
        HabitDeclaration saved = habitRepository.save(habit);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<HabitDeclarationResponse> getAllHabits(User user) {
        return habitRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HabitDeclarationResponse> getActiveHabits(User user) {
        return habitRepository.findByUser(user).stream()
                .filter(h -> h.getIsActive() != null && h.getIsActive())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HabitDeclarationResponse getHabitById(Long id, User user) {
        HabitDeclaration habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + id));
        return mapToResponse(habit);
    }

    @Transactional
    public HabitDeclarationResponse updateHabit(Long id, HabitDeclarationRequest request, User user) {
        HabitDeclaration habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + id));
        
        habit.setName(request.getName());
        habit.setDescription(request.getDescription());
        habit.setFrequency(request.getFrequency());
        habit.setExpectedBehavior(request.getExpectedBehavior());
        habit.setConflictingBehavior(request.getConflictingBehavior());
        
        HabitDeclaration updated = habitRepository.save(habit);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteHabit(Long id, User user) {
        HabitDeclaration habit = habitRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + id));
        
        // Soft delete - mark as inactive instead of deleting
        habit.setIsActive(false);
        habitRepository.save(habit);
    }

    @Transactional(readOnly = true)
    public List<HabitDeclarationResponse> getHabitsByFrequency(HabitDeclaration.Frequency frequency, User user) {
        return habitRepository.findByUser(user).stream()
                .filter(h -> h.getFrequency() == frequency)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HabitDeclarationResponse mapToResponse(HabitDeclaration habit) {
        HabitDeclarationResponse response = new HabitDeclarationResponse();
        response.setId(habit.getId());
        response.setName(habit.getName());
        response.setDescription(habit.getDescription());
        response.setFrequency(habit.getFrequency());
        response.setExpectedBehavior(habit.getExpectedBehavior());
        response.setConflictingBehavior(habit.getConflictingBehavior());
        response.setCreatedAt(habit.getCreatedAt());
        response.setUpdatedAt(habit.getUpdatedAt());
        response.setIsActive(habit.getIsActive());
        return response;
    }
}
