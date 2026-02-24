package com.example.demo.controller;

import com.example.demo.dto.BehaviorLogRequest;
import com.example.demo.dto.BehaviorLogResponse;
import com.example.demo.entity.User;
import com.example.demo.service.BehaviorLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/behavior-logs")
@RequiredArgsConstructor
public class BehaviorLogController {

    private final BehaviorLogService behaviorLogService;

    @PostMapping
    public ResponseEntity<BehaviorLogResponse> createBehaviorLog(
            @Valid @RequestBody BehaviorLogRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        BehaviorLogResponse response = behaviorLogService.createBehaviorLog(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BehaviorLogResponse>> getAllBehaviorLogs(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<BehaviorLogResponse> logs = behaviorLogService.getAllBehaviorLogs(user);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BehaviorLogResponse> getBehaviorLogById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        BehaviorLogResponse response = behaviorLogService.getBehaviorLogById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<BehaviorLogResponse>> getBehaviorLogsByHabitId(
            @PathVariable Long habitId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<BehaviorLogResponse> logs = behaviorLogService.getBehaviorLogsByHabitId(habitId, user);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/habit/{habitId}/range")
    public ResponseEntity<List<BehaviorLogResponse>> getBehaviorLogsByDateRange(
            @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<BehaviorLogResponse> logs = behaviorLogService.getBehaviorLogsByHabitIdAndDateRange(habitId, start, end, user);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/habit/{habitId}/recent")
    public ResponseEntity<List<BehaviorLogResponse>> getRecentBehaviorLogs(
            @PathVariable Long habitId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<BehaviorLogResponse> logs = behaviorLogService.getRecentBehaviorLogs(habitId, user);
        return ResponseEntity.ok(logs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BehaviorLogResponse> updateBehaviorLog(
            @PathVariable Long id, 
            @Valid @RequestBody BehaviorLogRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        BehaviorLogResponse response = behaviorLogService.updateBehaviorLog(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBehaviorLog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        behaviorLogService.deleteBehaviorLog(id, user);
        return ResponseEntity.noContent().build();
    }
}
