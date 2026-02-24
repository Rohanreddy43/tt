package com.example.demo.dto;

import com.example.demo.entity.Contradiction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContradictionResponse {
    
    private Long id;
    private Long habitId;
    private Long behaviorLogId;
    private String description;
    private Contradiction.ContradictionType contradictionType;
    private Contradiction.Severity severity;
    private String recommendation;
    private LocalDateTime detectedAt;
}
