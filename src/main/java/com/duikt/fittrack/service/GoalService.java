package com.duikt.fittrack.service;

import com.duikt.fittrack.domain.GoalDTO;

import java.util.List;
import java.util.UUID;

public interface GoalService {
    List<GoalDTO> findAllGoals();
    GoalDTO findGoalById(UUID id);
    GoalDTO createGoal(GoalDTO goalDTO, UUID userId);
    GoalDTO updateGoal(UUID id, GoalDTO goalDTO);
    void deleteGoal(UUID id);
}
