package com.example.demo.dto;

import com.example.demo.entity.BehaviorLog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorLogRequest {

    @NotNull(message = "Habit ID is required")
    private Long habitId;
    
    @NotBlank(message = "Action description is required")
    private String action;
    
    @NotNull(message = "Timestamp is required")
    private LocalDateTime timestamp;
    
    private String notes;
    
    private BehaviorLog.ActionType actionType;
}
