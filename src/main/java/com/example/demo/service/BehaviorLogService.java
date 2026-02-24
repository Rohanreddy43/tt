package com.example.demo.service;

import com.example.demo.dto.BehaviorLogRequest;
import com.example.demo.dto.BehaviorLogResponse;
import com.example.demo.entity.BehaviorLog;
import com.example.demo.entity.HabitDeclaration;
import com.example.demo.entity.User;
import com.example.demo.repository.BehaviorLogRepository;
import com.example.demo.repository.HabitDeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BehaviorLogService {

    private final BehaviorLogRepository behaviorLogRepository;
    private final HabitDeclarationRepository habitRepository;

    @Transactional
    public BehaviorLogResponse createBehaviorLog(BehaviorLogRequest request, User user) {
        // Verify habit exists and belongs to user
        Long habitIdVal = request.getHabitId();
        HabitDeclaration habit = habitRepository.findByIdAndUserId(habitIdVal, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitIdVal));
        
        BehaviorLog behaviorLog = new BehaviorLog();
        behaviorLog.setHabitId(request.getHabitId());
        behaviorLog.setAction(request.getAction());
        behaviorLog.setTimestamp(request.getTimestamp());
        behaviorLog.setNotes(request.getNotes());
        behaviorLog.setActionType(request.getActionType() != null ? request.getActionType() : BehaviorLog.ActionType.NEUTRAL);
        behaviorLog.setUser(user);
        
        BehaviorLog saved = behaviorLogRepository.save(behaviorLog);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BehaviorLogResponse> getAllBehaviorLogs(User user) {
        return behaviorLogRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BehaviorLogResponse getBehaviorLogById(Long id, User user) {
        BehaviorLog behaviorLog = behaviorLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Behavior log not found with id: " + id));
        
        // Verify ownership
        if (!behaviorLog.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Behavior log not found with id: " + id);
        }
        
        return mapToResponse(behaviorLog);
    }

    @Transactional(readOnly = true)
    public List<BehaviorLogResponse> getBehaviorLogsByHabitId(Long habitId, User user) {
        // Verify habit belongs to user
        habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));
        
        return behaviorLogRepository.findByUserIdAndHabitId(user.getId(), habitId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BehaviorLogResponse> getBehaviorLogsByHabitIdAndDateRange(Long habitId, LocalDateTime start, LocalDateTime end, User user) {
        // Verify habit belongs to user
        habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));
        
        return behaviorLogRepository.findByUserIdAndHabitId(user.getId(), habitId).stream()
                .filter(b -> b.getTimestamp().isAfter(start) && b.getTimestamp().isBefore(end))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BehaviorLogResponse> getRecentBehaviorLogs(Long habitId, User user) {
        // Verify habit belongs to user
        habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));
        
        return behaviorLogRepository.findRecentByUserIdAndHabitId(user.getId(), habitId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BehaviorLogResponse updateBehaviorLog(Long id, BehaviorLogRequest request, User user) {
        BehaviorLog behaviorLog = behaviorLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Behavior log not found with id: " + id));
        
        // Verify ownership
        if (!behaviorLog.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Behavior log not found with id: " + id);
        }
        
        // Verify habit belongs to user
        Long reqHabitId = request.getHabitId();
        habitRepository.findByIdAndUserId(reqHabitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + reqHabitId));
        
        behaviorLog.setAction(request.getAction());
        behaviorLog.setTimestamp(request.getTimestamp());
        behaviorLog.setNotes(request.getNotes());
        behaviorLog.setActionType(request.getActionType());
        
        BehaviorLog updated = behaviorLogRepository.save(behaviorLog);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteBehaviorLog(Long id, User user) {
        BehaviorLog behaviorLog = behaviorLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Behavior log not found with id: " + id));
        
        // Verify ownership
        if (!behaviorLog.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Behavior log not found with id: " + id);
        }
        
        behaviorLogRepository.delete(behaviorLog);
    }

    private BehaviorLogResponse mapToResponse(BehaviorLog behaviorLog) {
        BehaviorLogResponse response = new BehaviorLogResponse();
        response.setId(behaviorLog.getId());
        response.setHabitId(behaviorLog.getHabitId());
        
        // Get habit name
        String habitName = habitRepository.findById(behaviorLog.getHabitId())
                .map(HabitDeclaration::getName)
                .orElse("Unknown");
        response.setHabitName(habitName);
        
        response.setAction(behaviorLog.getAction());
        response.setTimestamp(behaviorLog.getTimestamp());
        response.setNotes(behaviorLog.getNotes());
        response.setActionType(behaviorLog.getActionType());
        response.setLoggedAt(behaviorLog.getLoggedAt());
        return response;
    }
}
