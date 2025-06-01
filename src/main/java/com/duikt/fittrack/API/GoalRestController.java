package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.GoalDTO;
import com.duikt.fittrack.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalRestController {

    private final GoalService goalService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<GoalDTO>> getGoals() {
        return ResponseEntity.ok(goalService.findAllGoals());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GoalDTO> getGoal(@PathVariable UUID id) {
        return ResponseEntity.ok(goalService.findGoalById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GoalDTO> createGoal(@Valid @RequestBody GoalDTO goalDTO, @RequestParam UUID userId) {
        return new ResponseEntity<>(goalService.createGoal(goalDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<GoalDTO> updateGoal(@PathVariable UUID id, @Valid @RequestBody GoalDTO goalDTO) {
        return ResponseEntity.ok(goalService.updateGoal(id, goalDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable UUID id) {
        goalService.deleteGoal(id);
    }
}
