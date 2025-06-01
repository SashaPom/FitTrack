package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.enums.Type;
import com.duikt.fittrack.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsRestController {

    private final StatsService statsService;

    @GetMapping("/workouts/by-type")
    public ResponseEntity<Map<Type, Map<String, Integer>>> getWorkoutStatsByType() {
        Map<Type, Map<String, Integer>> stats = statsService.getWorkoutStatsByType();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/progress/calories")
    public ResponseEntity<Map<LocalDate, Double>> getCaloriesProgress(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Map<LocalDate, Double> progressStats = statsService.getCaloriesProgress(from, to);
        return ResponseEntity.ok(progressStats);
    }
}
