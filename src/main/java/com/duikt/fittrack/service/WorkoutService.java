package com.duikt.fittrack.service;

import com.duikt.fittrack.domain.WorkoutDTO;

import java.util.List;
import java.util.UUID;

public interface WorkoutService {
    List<WorkoutDTO> findAllWorkouts();
    WorkoutDTO findWorkoutById(UUID id);
    WorkoutDTO createWorkout(WorkoutDTO workoutDTO, UUID userId);
    WorkoutDTO updateWorkout(UUID id, WorkoutDTO workoutDTO);
    void deleteWorkout(UUID id);
}
