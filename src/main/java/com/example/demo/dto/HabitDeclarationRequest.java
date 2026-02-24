package com.example.demo.dto;

import com.example.demo.entity.HabitDeclaration;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitDeclarationRequest {

    @NotBlank(message = "Habit name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Frequency is required")
    private HabitDeclaration.Frequency frequency;
    
    private String expectedBehavior;
    
    private String conflictingBehavior;
}
