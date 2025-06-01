package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.enums.Type;
import com.duikt.fittrack.entity.WorkoutEntity;
import com.duikt.fittrack.entity.ProgressTrackerEntity;
import com.duikt.fittrack.repository.WorkoutRepository;
import com.duikt.fittrack.repository.ProgressTrackerRepository;
import com.duikt.fittrack.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class StatsServiceImplTest {

    @Mock
    private WorkoutRepository workoutRepository;
    @Mock
    private ProgressTrackerRepository progressTrackerRepository;

    private StatsServiceImpl statsService;

    @BeforeEach
    void setUp() {
        statsService = new StatsServiceImpl(workoutRepository, progressTrackerRepository);
    }

    @Test
    void testGetWorkoutStatsByType() {
        WorkoutEntity workout1 = WorkoutEntity.builder()
                .id(UUID.randomUUID())
                .type(Type.RUNNING)
                .date(LocalDate.now())
                .duration(30)
                .caloriesBurned(300)
                .build();
        WorkoutEntity workout2 = WorkoutEntity.builder()
                .id(UUID.randomUUID())
                .type(Type.RUNNING)
                .date(LocalDate.now())
                .duration(45)
                .caloriesBurned(450)
                .build();
        List<WorkoutEntity> workoutEntities = List.of(workout1, workout2);

        when(workoutRepository.findAll()).thenReturn(workoutEntities);

        Map<Type, Map<String, Integer>> result = statsService.getWorkoutStatsByType();

        assertNotNull(result);
        assertTrue(result.containsKey(Type.RUNNING));
        Map<String, Integer> stats = result.get(Type.RUNNING);
        assertEquals(2, stats.get("count").intValue());
        assertEquals(75, stats.get("duration").intValue());
        assertEquals(750, stats.get("calories").intValue());
    }

    @Test
    void testGetCaloriesProgress() {
        LocalDate from = LocalDate.of(2022, 1, 1);
        LocalDate to = LocalDate.of(2022, 1, 3);

        ProgressTrackerEntity progress1 = ProgressTrackerEntity.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.of(2022, 1, 1))
                .weight(70.0)
                .fatPercentage(20.0)
                .muscleMass(50.0)
                .build();
        ProgressTrackerEntity progress2 = ProgressTrackerEntity.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.of(2022, 1, 1))
                .weight(80.0)
                .fatPercentage(21.0)
                .muscleMass(51.0)
                .build();

        ProgressTrackerEntity progress3 = ProgressTrackerEntity.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.of(2022, 1, 2))
                .weight(75.0)
                .fatPercentage(19.0)
                .muscleMass(52.0)
                .build();

        List<ProgressTrackerEntity> progressEntities = List.of(progress1, progress2, progress3);

        when(progressTrackerRepository.findByDateBetween(from, to)).thenReturn(progressEntities);

        Map<LocalDate, Double> progressResult = statsService.getCaloriesProgress(from, to);

        assertNotNull(progressResult);
        assertEquals(2, progressResult.size());
        assertEquals(750.0, progressResult.get(LocalDate.of(2022, 1, 1)));
        assertEquals(750.0, progressResult.get(LocalDate.of(2022, 1, 2)));
        assertFalse(progressResult.containsKey(LocalDate.of(2022, 1, 3)));
    }
}
