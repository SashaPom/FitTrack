package com.duikt.fittrack.service.impl;

import com.duikt.fittrack.domain.NutritionLogDTO;
import com.duikt.fittrack.entity.NutritionLogEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.NutritionLogNotFoundException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.NutritionLogMapper;
import com.duikt.fittrack.repository.NutritionLogRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.NutritionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NutritionLogServiceImpl implements NutritionLogService {

    private final NutritionLogRepository nutritionLogRepository;
    private final UserRepository userRepository;
    private final NutritionLogMapper nutritionLogMapper;

    @Transactional(readOnly = true)
    public
    List<NutritionLogDTO> findAllLogs() {
        return nutritionLogMapper.toNutritionDtoList(nutritionLogRepository.findAll());
    }

    @Transactional(readOnly = true)
    public NutritionLogDTO findLogById(UUID id) {
        return nutritionLogMapper.toNutritionDto(nutritionLogRepository.findById(id)
                .orElseThrow(() -> new NutritionLogNotFoundException("Nutrition log not found: " + id)));
    }

    @Override
    @Transactional
    public NutritionLogDTO createLog(NutritionLogDTO nutritionLogDTO, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        NutritionLogEntity nutritionLog = nutritionLogMapper.toNutritionEntity(nutritionLogDTO)
                .toBuilder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        return nutritionLogMapper.toNutritionDto(nutritionLogRepository.save(nutritionLog));
    }

    @Override
    @Transactional
    public NutritionLogDTO updateLog(UUID id, NutritionLogDTO updatedLog) {
        NutritionLogEntity existingLog = nutritionLogRepository.findById(id)
                .orElseThrow(() -> new NutritionLogNotFoundException("Nutrition log not found with id: " + id));

        NutritionLogEntity logWithUpdates = existingLog.toBuilder()
                .date(updatedLog.getDate())
                .totalCalories(updatedLog.getTotalCalories())
                .build();

        return nutritionLogMapper.toNutritionDto(nutritionLogRepository.save(logWithUpdates));
    }

    @Override
    @Transactional
    public void deleteLog(UUID id) {
        if (!nutritionLogRepository.existsById(id)) {
            throw new NutritionLogNotFoundException("Nutrition log not found with id: " + id);
        }
        nutritionLogRepository.deleteById(id);
    }
}
