package com.duikt.fittrack.service;

import com.duikt.fittrack.domain.enums.Type;
import java.time.LocalDate;
import java.util.Map;

public interface StatsService {
    Map<Type, Map<String, Integer>> getWorkoutStatsByType();
    Map<LocalDate, Double> getCaloriesProgress(LocalDate from, LocalDate to);
}
