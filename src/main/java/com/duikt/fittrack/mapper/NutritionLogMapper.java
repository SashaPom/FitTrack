package com.duikt.fittrack.mapper;

import com.duikt.fittrack.domain.NutritionLogDTO;
import com.duikt.fittrack.entity.NutritionLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NutritionLogMapper {

    public NutritionLogDTO toNutritionDto(NutritionLogEntity entity) {
        return NutritionLogDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .totalCalories(entity.getTotalCalories())
                .build();
    }

    public NutritionLogEntity toNutritionEntity(NutritionLogDTO dto) {
        return NutritionLogEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .totalCalories(dto.getTotalCalories())
                .build();
    }

    public List<NutritionLogDTO> toNutritionDtoList(List<NutritionLogEntity> entities) {
        return entities.stream()
                .map(this::toNutritionDto)
                .collect(Collectors.toList());
    }
}
