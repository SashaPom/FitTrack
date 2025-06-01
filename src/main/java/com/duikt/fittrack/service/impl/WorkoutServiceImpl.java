package com.duikt.fittrack.service.impl;

import com.duikt.fittrack.domain.WorkoutDTO;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.entity.WorkoutEntity;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.exception.WorkoutNotFoundException;
import com.duikt.fittrack.mapper.WorkoutMapper;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.repository.WorkoutRepository;
import com.duikt.fittrack.service.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final  WorkoutMapper workoutMapper;

    @Transactional(readOnly = true)
    public List<WorkoutDTO> findAllWorkouts() {
        return workoutMapper.toWorkoutDtoList(workoutRepository.findAll());
    }

    @Transactional(readOnly = true)
    public WorkoutDTO findWorkoutById(UUID id) {
        return workoutMapper.toWorkoutDto(workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found: " + id)));
    }

    @Override
    @Transactional
    public WorkoutDTO createWorkout(WorkoutDTO workoutDTO, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        WorkoutEntity workout = workoutMapper.toWorkoutEntity(workoutDTO)
                .toBuilder()
                .user(user)
                .date(workoutDTO.getDate())
                .type(workoutDTO.getType())
                .duration(workoutDTO.getDuration())
                .caloriesBurned(workoutDTO.getCaloriesBurned())
                .build();

        return workoutMapper.toWorkoutDto(workoutRepository.save(workout));
    }

    @Override
    @Transactional
    public WorkoutDTO updateWorkout(UUID id, WorkoutDTO updatedWorkout) {
        WorkoutEntity existingWorkout = workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException("Workout not found with id: " + id));

        WorkoutEntity workoutWithUpdates = existingWorkout.toBuilder()
                .type(updatedWorkout.getType())
                .date(updatedWorkout.getDate())
                .duration(updatedWorkout.getDuration())
                .caloriesBurned(updatedWorkout.getCaloriesBurned())
                .build();

        return workoutMapper.toWorkoutDto(workoutRepository.save(workoutWithUpdates));
    }

    @Override
    @Transactional
    public void deleteWorkout(UUID id) {
        if (!workoutRepository.existsById(id)) {
            throw new WorkoutNotFoundException("Workout not found with id: " + id);
        }
        workoutRepository.deleteById(id);
    }
}
