package com.example.demo.controller;

import com.example.demo.dto.AnalysisSummary;
import com.example.demo.dto.ContradictionResponse;
import com.example.demo.entity.User;
import com.example.demo.service.ContradictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contradictions")
@RequiredArgsConstructor
public class ContradictionController {

    private final ContradictionService contradictionService;

    @GetMapping
    public ResponseEntity<List<ContradictionResponse>> getAllContradictions(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<ContradictionResponse> contradictions = contradictionService.getAllContradictions(user);
        return ResponseEntity.ok(contradictions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContradictionResponse> getContradictionById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        ContradictionResponse response = contradictionService.getContradictionById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<ContradictionResponse>> getContradictionsByHabitId(
            @PathVariable Long habitId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        List<ContradictionResponse> contradictions = contradictionService.getContradictionsByHabitId(habitId, user);
        return ResponseEntity.ok(contradictions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContradiction(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        contradictionService.deleteContradiction(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/habit/{habitId}/analysis")
    public ResponseEntity<AnalysisSummary> getAnalysisSummary(
            @PathVariable Long habitId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        AnalysisSummary summary = contradictionService.generateAnalysisSummary(habitId, user);
        return ResponseEntity.ok(summary);
    }
}
