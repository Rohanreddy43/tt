package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisSummary {
    
    private Long habitId;
    private String habitName;
    private int totalBehaviorLogs;
    private int completedActions;
    private int missedActions;
    private int conflictingActions;
    private int contradictionsDetected;
    private double consistencyScore;
    private List<String> recommendations;
}
