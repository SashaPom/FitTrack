package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.WorkoutDTO;
import com.duikt.fittrack.domain.enums.Type;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.entity.WorkoutEntity;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.exception.WorkoutNotFoundException;
import com.duikt.fittrack.mapper.WorkoutMapper;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.repository.WorkoutRepository;
import com.duikt.fittrack.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class WorkoutServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkoutMapper workoutMapper;

    private WorkoutServiceImpl workoutService;
    private UUID workoutId;
    private UUID userId;
    private WorkoutEntity workoutEntity;
    private WorkoutDTO workoutDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        workoutService = new WorkoutServiceImpl(workoutRepository, userRepository, workoutMapper);
        workoutId = UUID.randomUUID();
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        workoutEntity = WorkoutEntity.builder()
                .id(workoutId)
                .type(Type.CYCLING)
                .date(LocalDate.now())
                .duration(60)
                .caloriesBurned(500)
                .user(userEntity)
                .build();

        workoutDTO = WorkoutDTO.builder()
                .id(workoutId)
                .type(Type.CYCLING)
                .date(LocalDate.now())
                .duration(60)
                .caloriesBurned(500)
                .build();
    }

    @Test
    void testFindAllWorkouts() {
        List<WorkoutEntity> entities = List.of(workoutEntity);
        List<WorkoutDTO> dtos = List.of(workoutDTO);

        when(workoutRepository.findAll()).thenReturn(entities);
        when(workoutMapper.toWorkoutDtoList(entities)).thenReturn(dtos);

        List<WorkoutDTO> result = workoutService.findAllWorkouts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(workoutRepository).findAll();
        verify(workoutMapper).toWorkoutDtoList(entities);
    }

    @Test
    void testFindWorkoutById() {
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workoutEntity));
        when(workoutMapper.toWorkoutDto(workoutEntity)).thenReturn(workoutDTO);

        WorkoutDTO result = workoutService.findWorkoutById(workoutId);

        assertNotNull(result);
        assertEquals(workoutId, result.getId());
        verify(workoutRepository).findById(workoutId);
        verify(workoutMapper).toWorkoutDto(workoutEntity);
    }

    @Test
    void testFindWorkoutByIdNotFound() {
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.empty());

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.findWorkoutById(workoutId));
        verify(workoutRepository).findById(workoutId);
    }

    @Test
    void testCreateWorkout() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        WorkoutEntity baseEntity = WorkoutEntity.builder()
                .date(workoutDTO.getDate())
                .duration(workoutDTO.getDuration())
                .caloriesBurned(workoutDTO.getCaloriesBurned())
                .build();
        when(workoutMapper.toWorkoutEntity(workoutDTO)).thenReturn(baseEntity);

        WorkoutEntity savedEntity = baseEntity.toBuilder()
                .user(userEntity)
                .date(workoutDTO.getDate())
                .type(workoutDTO.getType())
                .duration(workoutDTO.getDuration())
                .caloriesBurned(workoutDTO.getCaloriesBurned())
                .build();

        when(workoutRepository.save(any(WorkoutEntity.class))).thenReturn(workoutEntity);
        when(workoutMapper.toWorkoutDto(workoutEntity)).thenReturn(workoutDTO);

        WorkoutDTO result = workoutService.createWorkout(workoutDTO, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(workoutMapper).toWorkoutEntity(workoutDTO);
        verify(workoutRepository).save(any(WorkoutEntity.class));
        verify(workoutMapper).toWorkoutDto(workoutEntity);
    }

    @Test
    void testUpdateWorkout() {
        when(workoutRepository.findById(workoutId)).thenReturn(Optional.of(workoutEntity));

        WorkoutDTO updatedDTO = WorkoutDTO.builder()
                .id(workoutId)
                .type(Type.CYCLING)
                .date(workoutDTO.getDate().plusDays(1))
                .duration(90)
                .caloriesBurned(600)
                .build();

        WorkoutEntity updatedEntity = workoutEntity.toBuilder()
                .type(updatedDTO.getType())
                .date(updatedDTO.getDate())
                .duration(updatedDTO.getDuration())
                .caloriesBurned(updatedDTO.getCaloriesBurned())
                .build();

        when(workoutRepository.save(any(WorkoutEntity.class))).thenReturn(updatedEntity);
        when(workoutMapper.toWorkoutDto(updatedEntity)).thenReturn(updatedDTO);

        WorkoutDTO result = workoutService.updateWorkout(workoutId, updatedDTO);

        assertNotNull(result);
        assertEquals(90, result.getDuration());
        verify(workoutRepository).findById(workoutId);
        verify(workoutRepository).save(any(WorkoutEntity.class));
        verify(workoutMapper).toWorkoutDto(updatedEntity);
    }

    @Test
    void testDeleteWorkout() {
        when(workoutRepository.existsById(workoutId)).thenReturn(true);

        workoutService.deleteWorkout(workoutId);

        verify(workoutRepository).existsById(workoutId);
        verify(workoutRepository).deleteById(workoutId);
    }

    @Test
    void testDeleteWorkoutNotFound() {
        when(workoutRepository.existsById(workoutId)).thenReturn(false);

        assertThrows(WorkoutNotFoundException.class, () -> workoutService.deleteWorkout(workoutId));
        verify(workoutRepository).existsById(workoutId);
    }
}
