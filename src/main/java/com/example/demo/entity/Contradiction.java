package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contradictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contradiction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "habit_id", nullable = false)
    private Long habitId;

    @Column(name = "behavior_log_id")
    private Long behaviorLogId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "contradiction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContradictionType contradictionType;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @CreationTimestamp
    @Column(name = "detected_at", updatable = false)
    private LocalDateTime detectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum ContradictionType {
        MISSED_ROUTINE,         // Expected behavior didn't happen
        BROKEN_COMMITMENT,      // Failed to follow through
        CONFLICTING_ACTION,     // Did something contrary to habit
        INCONSISTENT_BEHAVIOR,  // Behavior varies significantly
        PATTERN_VIOLATION       // Broke an established pattern
    }

    public enum Severity {
        LOW,
        MEDIUM,
        HIGH
    }
}
