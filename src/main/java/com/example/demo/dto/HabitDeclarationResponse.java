package com.example.demo.dto;

import com.example.demo.entity.HabitDeclaration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDeclarationResponse {
    
    private Long id;
    private String name;
    private String description;
    private HabitDeclaration.Frequency frequency;
    private String expectedBehavior;
    private String conflictingBehavior;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
