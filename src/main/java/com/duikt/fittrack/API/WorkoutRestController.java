package com.duikt.fittrack.API;

import com.duikt.fittrack.domain.WorkoutDTO;
import com.duikt.fittrack.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
public class WorkoutRestController {

    private final WorkoutService workoutService;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkoutDTO>> getWorkouts() {
        return ResponseEntity.ok(workoutService.findAllWorkouts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WorkoutDTO> getWorkout(@PathVariable UUID id) {
        return ResponseEntity.ok(workoutService.findWorkoutById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WorkoutDTO> createWorkout(@Valid @RequestBody WorkoutDTO workoutDTO, @RequestParam UUID userId) {
        return new ResponseEntity<>(workoutService.createWorkout(workoutDTO, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WorkoutDTO> updateWorkout(@PathVariable UUID id, @Valid @RequestBody WorkoutDTO workoutDTO) {
        return ResponseEntity.ok(workoutService.updateWorkout(id, workoutDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkout(@PathVariable UUID id) {
        workoutService.deleteWorkout(id);
    }
}
