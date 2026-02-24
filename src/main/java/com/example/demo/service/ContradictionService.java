package com.example.demo.service;

import com.example.demo.dto.AnalysisSummary;
import com.example.demo.dto.ContradictionResponse;
import com.example.demo.entity.BehaviorLog;
import com.example.demo.entity.Contradiction;
import com.example.demo.entity.HabitDeclaration;
import com.example.demo.entity.User;
import com.example.demo.repository.BehaviorLogRepository;
import com.example.demo.repository.ContradictionRepository;
import com.example.demo.repository.HabitDeclarationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContradictionService {

    private final ContradictionRepository contradictionRepository;
    private final BehaviorLogRepository behaviorLogRepository;
    private final HabitDeclarationRepository habitRepository;

    @Transactional
    public Contradiction detectContradiction(Long habitId, BehaviorLog behaviorLog, User user) {
        HabitDeclaration habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));

        Contradiction contradiction = new Contradiction();
        contradiction.setHabitId(habitId);
        contradiction.setBehaviorLogId(behaviorLog.getId());
        contradiction.setUser(user);

        // Analyze behavior and detect contradiction type
        if (behaviorLog.getActionType() == BehaviorLog.ActionType.MISSED) {
            contradiction.setContradictionType(Contradiction.ContradictionType.MISSED_ROUTINE);
            contradiction.setDescription("Missed routine for habit: " + habit.getName());
            contradiction.setSeverity(Contradiction.Severity.MEDIUM);
            contradiction.setRecommendation("Try setting reminders or breaking down the habit into smaller steps.");
        } else if (behaviorLog.getActionType() == BehaviorLog.ActionType.CONFLICTING) {
            contradiction.setContradictionType(Contradiction.ContradictionType.CONFLICTING_ACTION);
            contradiction.setDescription("Conflicting action detected for habit: " + habit.getName());
            contradiction.setSeverity(Contradiction.Severity.HIGH);
            contradiction.setRecommendation("Identify triggers that lead to conflicting behaviors and avoid them.");
        } else if (behaviorLog.getActionType() == BehaviorLog.ActionType.NEUTRAL) {
            // Check for pattern violations by analyzing recent behavior
            List<BehaviorLog> recentLogs = behaviorLogRepository.findRecentByUserIdAndHabitId(user.getId(), habitId);
            long completedCount = recentLogs.stream()
                    .filter(log -> log.getActionType() == BehaviorLog.ActionType.COMPLETED)
                    .count();
            
            if (recentLogs.size() >= 3 && completedCount == 0) {
                contradiction.setContradictionType(Contradiction.ContradictionType.INCONSISTENT_BEHAVIOR);
                contradiction.setDescription("Inconsistent behavior pattern detected for: " + habit.getName());
                contradiction.setSeverity(Contradiction.Severity.MEDIUM);
                contradiction.setRecommendation("Focus on building consistency by starting with smaller commitments.");
            } else {
                return null; // No contradiction detected
            }
        } else {
            return null; // No contradiction for completed actions
        }

        return contradictionRepository.save(contradiction);
    }

    @Transactional
    public ContradictionResponse analyzeBehaviorAndCreateContradiction(Long habitId, BehaviorLog behaviorLog, User user) {
        Contradiction contradiction = detectContradiction(habitId, behaviorLog, user);
        if (contradiction != null) {
            return mapToResponse(contradiction);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ContradictionResponse> getAllContradictions(User user) {
        return contradictionRepository.findAll().stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContradictionResponse> getContradictionsByHabitId(Long habitId, User user) {
        // Verify habit belongs to user
        habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));
        
        return contradictionRepository.findByHabitId(habitId).stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContradictionResponse getContradictionById(Long id, User user) {
        Contradiction contradiction = contradictionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contradiction not found with id: " + id));
        
        // Verify ownership
        if (contradiction.getUser() == null || !contradiction.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Contradiction not found with id: " + id);
        }
        
        return mapToResponse(contradiction);
    }

    @Transactional
    public void deleteContradiction(Long id, User user) {
        Contradiction contradiction = contradictionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contradiction not found with id: " + id));
        
        // Verify ownership
        if (contradiction.getUser() == null || !contradiction.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Contradiction not found with id: " + id);
        }
        
        contradictionRepository.delete(contradiction);
    }

    @Transactional(readOnly = true)
    public AnalysisSummary generateAnalysisSummary(Long habitId, User user) {
        // Verify habit belongs to user
        HabitDeclaration habit = habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Habit not found with id: " + habitId));

        List<BehaviorLog> behaviorLogs = behaviorLogRepository.findByUserIdAndHabitId(user.getId(), habitId);
        List<Contradiction> contradictions = contradictionRepository.findByHabitId(habitId).stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());

        int totalLogs = behaviorLogs.size();
        long completedCount = behaviorLogs.stream()
                .filter(log -> log.getActionType() == BehaviorLog.ActionType.COMPLETED)
                .count();
        long missedCount = behaviorLogs.stream()
                .filter(log -> log.getActionType() == BehaviorLog.ActionType.MISSED)
                .count();
        long conflictingCount = behaviorLogs.stream()
                .filter(log -> log.getActionType() == BehaviorLog.ActionType.CONFLICTING)
                .count();

        // Calculate consistency score (0-100)
        double consistencyScore = totalLogs > 0 ? (completedCount * 100.0 / totalLogs) : 0;

        // Generate recommendations
        List<String> recommendations = generateRecommendations(habit, completedCount, missedCount, conflictingCount, consistencyScore);

        AnalysisSummary summary = new AnalysisSummary();
        summary.setHabitId(habitId);
        summary.setHabitName(habit.getName());
        summary.setTotalBehaviorLogs(totalLogs);
        summary.setCompletedActions((int) completedCount);
        summary.setMissedActions((int) missedCount);
        summary.setConflictingActions((int) conflictingCount);
        summary.setContradictionsDetected(contradictions.size());
        summary.setConsistencyScore(Math.round(consistencyScore * 100.0) / 100.0);
        summary.setRecommendations(recommendations);

        return summary;
    }

    private List<String> generateRecommendations(HabitDeclaration habit, long completed, long missed, long conflicting, double score) {
        List<String> recommendations = new ArrayList<>();

        if (score >= 80) {
            recommendations.add("Great job! Keep up the consistent behavior.");
        } else if (score >= 50) {
            recommendations.add("You're making progress. Try to increase consistency by setting smaller daily goals.");
        } else {
            recommendations.add("Consider breaking down this habit into smaller, more manageable steps.");
        }

        if (missed > completed) {
            recommendations.add("You've missed more than you've completed. Try adding reminders or changing the time of day.");
        }

        if (conflicting > 0) {
            recommendations.add("Identify triggers that lead to conflicting behaviors and develop strategies to avoid them.");
        }

        if (habit.getExpectedBehavior() != null && !habit.getExpectedBehavior().isEmpty()) {
            recommendations.add("Remember your commitment: " + habit.getExpectedBehavior());
        }

        return recommendations;
    }

    private ContradictionResponse mapToResponse(Contradiction contradiction) {
        ContradictionResponse response = new ContradictionResponse();
        response.setId(contradiction.getId());
        response.setHabitId(contradiction.getHabitId());
        response.setBehaviorLogId(contradiction.getBehaviorLogId());
        response.setDescription(contradiction.getDescription());
        response.setContradictionType(contradiction.getContradictionType());
        response.setSeverity(contradiction.getSeverity());
        response.setRecommendation(contradiction.getRecommendation());
        response.setDetectedAt(contradiction.getDetectedAt());
        return response;
    }
}
