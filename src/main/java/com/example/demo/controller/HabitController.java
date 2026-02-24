package com.example.demo.controller;

import com.example.demo.dto.HabitDeclarationRequest;
import com.example.demo.dto.HabitDeclarationResponse;
import com.example.demo.entity.HabitDeclaration;
import com.example.demo.entity.User;
import com.example.demo.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitDeclarationResponse> createHabit(
            @Valid @RequestBody HabitDeclarationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        HabitDeclarationResponse response = habitService.createHabit(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitDeclarationResponse>> getAllHabits(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<HabitDeclarationResponse> habits = habitService.getAllHabits(user);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/active")
    public ResponseEntity<List<HabitDeclarationResponse>> getActiveHabits(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<HabitDeclarationResponse> habits = habitService.getActiveHabits(user);
        return ResponseEntity.ok(habits);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitDeclarationResponse> getHabitById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        HabitDeclarationResponse response = habitService.getHabitById(id, user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HabitDeclarationResponse> updateHabit(
            @PathVariable Long id, 
            @Valid @RequestBody HabitDeclarationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        HabitDeclarationResponse response = habitService.updateHabit(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        habitService.deleteHabit(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/frequency/{frequency}")
    public ResponseEntity<List<HabitDeclarationResponse>> getHabitsByFrequency(
            @PathVariable HabitDeclaration.Frequency frequency,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<HabitDeclarationResponse> habits = habitService.getHabitsByFrequency(frequency, user);
        return ResponseEntity.ok(habits);
    }
}
