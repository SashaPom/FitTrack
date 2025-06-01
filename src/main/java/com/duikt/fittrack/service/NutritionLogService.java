package com.duikt.fittrack.service;

import com.duikt.fittrack.domain.NutritionLogDTO;

import java.util.List;
import java.util.UUID;

public interface NutritionLogService {
    List<NutritionLogDTO> findAllLogs();
    NutritionLogDTO findLogById(UUID id);
    NutritionLogDTO createLog(NutritionLogDTO nutritionLogDTO, UUID userId);
    NutritionLogDTO updateLog(UUID id, NutritionLogDTO nutritionLogDTO);
    void deleteLog(UUID id);
}
