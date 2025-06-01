package com.duikt.fittrack.mapper;

import com.duikt.fittrack.domain.ProgressTrackerDTO;
import com.duikt.fittrack.entity.ProgressTrackerEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProgressTrackerMapper {

    public ProgressTrackerDTO toProgressDto(ProgressTrackerEntity entity) {
        return ProgressTrackerDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .weight(entity.getWeight())
                .fatPercentage(entity.getFatPercentage())
                .muscleMass(entity.getMuscleMass())
                .build();
    }

    public ProgressTrackerEntity toProgressEntity(ProgressTrackerDTO dto) {
        return ProgressTrackerEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .weight(dto.getWeight())
                .fatPercentage(dto.getFatPercentage())
                .muscleMass(dto.getMuscleMass())
                .build();
    }

    public List<ProgressTrackerDTO> toProgressDtoList(List<ProgressTrackerEntity> entities) {
        return entities.stream()
                .map(this::toProgressDto)
                .collect(Collectors.toList());
    }
}
