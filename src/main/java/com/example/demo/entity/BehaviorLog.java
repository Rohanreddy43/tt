package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "behavior_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Habit ID is required")
    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @NotBlank(message = "Action description is required")
    @Column(nullable = false)
    private String action;

    @NotNull(message = "Timestamp is required")
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "action_type")
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @CreationTimestamp
    @Column(name = "logged_at", updatable = false)
    private LocalDateTime loggedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum ActionType {
        COMPLETED,      // User performed the expected behavior
        MISSED,         // User missed the expected behavior
        CONFLICTING,    // User performed conflicting behavior
        NEUTRAL         // Neutral observation
    }
}
