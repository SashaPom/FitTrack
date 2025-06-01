package com.duikt.fittrack.service.impl;

import com.duikt.fittrack.domain.WorkoutDTO;
import com.duikt.fittrack.domain.ProgressTrackerDTO;
import com.duikt.fittrack.domain.enums.Type;
import com.duikt.fittrack.repository.WorkoutRepository;
import com.duikt.fittrack.repository.ProgressTrackerRepository;
import com.duikt.fittrack.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final WorkoutRepository workoutRepository;
    private final ProgressTrackerRepository progressTrackerRepository;

    @Override
    public Map<Type, Map<String, Integer>> getWorkoutStatsByType() {
        List<WorkoutDTO> workouts = workoutRepository.findAll().stream()
                .map(entity -> WorkoutDTO.builder()
                        .id(entity.getId())
                        .type(entity.getType())
                        .date(entity.getDate())
                        .duration(entity.getDuration())
                        .caloriesBurned(entity.getCaloriesBurned())
                        .build())
                .toList();

        return workouts.stream()
                .collect(Collectors.groupingBy(
                        WorkoutDTO::getType,
                        Collectors.collectingAndThen(Collectors.toList(), list -> {
                            int count = list.size();
                            int totalDuration = list.stream().mapToInt(WorkoutDTO::getDuration).sum();
                            int totalCalories = list.stream().mapToInt(WorkoutDTO::getCaloriesBurned).sum();
                            return Map.of(
                                    "count", count,
                                    "duration", totalDuration,
                                    "calories", totalCalories
                            );
                        })
                ));
    }

    @Override
    public Map<LocalDate, Double> getCaloriesProgress(LocalDate from, LocalDate to) {
        List<ProgressTrackerDTO> progressEntries = progressTrackerRepository.findByDateBetween(from, to).stream()
                .map(entity -> ProgressTrackerDTO.builder()
                        .id(entity.getId())
                        .date(entity.getDate())
                        .weight(entity.getWeight())
                        .fatPercentage(entity.getFatPercentage())
                        .muscleMass(entity.getMuscleMass())
                        .build())
                .toList();

        return progressEntries.stream()
                .collect(Collectors.groupingBy(
                        ProgressTrackerDTO::getDate,
                        Collectors.averagingDouble(entry -> entry.getWeight() * 10)
                ));
    }
}
