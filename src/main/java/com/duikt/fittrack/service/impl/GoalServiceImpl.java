package com.duikt.fittrack.service.impl;

import com.duikt.fittrack.domain.GoalDTO;
import com.duikt.fittrack.entity.GoalEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.GoalNotFoundException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.GoalMapper;
import com.duikt.fittrack.repository.GoalRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    @Transactional(readOnly = true)
    public List<GoalDTO> findAllGoals() {
        return goalMapper.toGoalDtoList(goalRepository.findAll());
    }

    @Transactional(readOnly = true)
    public GoalDTO findGoalById(UUID id) {
        return goalMapper.toGoalDto(goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found: " + id)));
    }

    @Override
    @Transactional
    public GoalDTO createGoal(GoalDTO goalDTO, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        GoalEntity goal = goalMapper.toGoalEntity(goalDTO)
                .toBuilder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return goalMapper.toGoalDto(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public GoalDTO updateGoal(UUID id, GoalDTO updatedGoal) {
        GoalEntity existingGoal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + id));

        GoalEntity goalWithUpdates = existingGoal.toBuilder()
                .name(updatedGoal.getName())
                .user(existingGoal.getUser())
                .type(updatedGoal.getType())
                .targetValue(updatedGoal.getTargetValue())
                .deadline(updatedGoal.getDeadline())
                .build();

        return goalMapper.toGoalDto(goalRepository.save(goalWithUpdates));
    }

    @Override
    @Transactional
    public void deleteGoal(UUID id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalNotFoundException("Goal not found with id: " + id);
        }
        goalRepository.deleteById(id);
    }
}

