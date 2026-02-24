package com.example.demo.dto;

import com.example.demo.entity.BehaviorLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorLogResponse {
    
    private Long id;
    private Long habitId;
    private String habitName;
    private String action;
    private LocalDateTime timestamp;
    private String notes;
    private BehaviorLog.ActionType actionType;
    private LocalDateTime loggedAt;
}
