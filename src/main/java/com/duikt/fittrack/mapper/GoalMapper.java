package com.duikt.fittrack.mapper;

import com.duikt.fittrack.domain.GoalDTO;
import com.duikt.fittrack.entity.GoalEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GoalMapper {

    public GoalDTO toGoalDto(GoalEntity entity) {
        return GoalDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .targetValue(entity.getTargetValue())
                .deadline(entity.getDeadline())
                .build();
    }

    public GoalEntity toGoalEntity(GoalDTO dto) {
        return GoalEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .targetValue(dto.getTargetValue())
                .deadline(dto.getDeadline())
                .build();
    }

    public List<GoalDTO> toGoalDtoList(List<GoalEntity> entities) {
        return entities.stream()
                .map(this::toGoalDto)
                .collect(Collectors.toList());
    }
}
