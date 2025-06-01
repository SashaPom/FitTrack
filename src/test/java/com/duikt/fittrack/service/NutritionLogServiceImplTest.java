package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.NutritionLogDTO;
import com.duikt.fittrack.entity.NutritionLogEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.NutritionLogNotFoundException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.NutritionLogMapper;
import com.duikt.fittrack.repository.NutritionLogRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.impl.NutritionLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class NutritionLogServiceImplTest {

    @Mock
    private NutritionLogRepository nutritionLogRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NutritionLogMapper nutritionLogMapper;

    private NutritionLogServiceImpl nutritionLogService;
    private UUID logId;
    private UUID userId;
    private NutritionLogEntity nutritionLogEntity;
    private NutritionLogDTO nutritionLogDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        nutritionLogService = new NutritionLogServiceImpl(nutritionLogRepository, userRepository, nutritionLogMapper);
        logId = UUID.randomUUID();
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        nutritionLogEntity = NutritionLogEntity.builder()
                .id(logId)
                .date(LocalDate.now())
                .totalCalories(500)
                .createdAt(LocalDateTime.now())
                .user(userEntity)
                .build();

        nutritionLogDTO = NutritionLogDTO.builder()
                .id(logId)
                .date(LocalDate.now())
                .totalCalories(500)
                .build();
    }

    @Test
    void testFindAllLogs() {
        List<NutritionLogEntity> entityList = List.of(nutritionLogEntity);
        List<NutritionLogDTO> dtoList = List.of(nutritionLogDTO);

        when(nutritionLogRepository.findAll()).thenReturn(entityList);
        when(nutritionLogMapper.toNutritionDtoList(entityList)).thenReturn(dtoList);

        List<NutritionLogDTO> result = nutritionLogService.findAllLogs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(500, result.get(0).getTotalCalories());
        verify(nutritionLogRepository).findAll();
        verify(nutritionLogMapper).toNutritionDtoList(entityList);
    }

    @Test
    void testFindLogById() {
        when(nutritionLogRepository.findById(logId)).thenReturn(Optional.of(nutritionLogEntity));
        when(nutritionLogMapper.toNutritionDto(nutritionLogEntity)).thenReturn(nutritionLogDTO);

        NutritionLogDTO result = nutritionLogService.findLogById(logId);

        assertNotNull(result);
        assertEquals(logId, result.getId());
        verify(nutritionLogRepository).findById(logId);
        verify(nutritionLogMapper).toNutritionDto(nutritionLogEntity);
    }

    @Test
    void testFindLogByIdNotFound() {
        when(nutritionLogRepository.findById(logId)).thenReturn(Optional.empty());
        assertThrows(NutritionLogNotFoundException.class, () -> nutritionLogService.findLogById(logId));
        verify(nutritionLogRepository).findById(logId);
    }

    @Test
    void testCreateLog() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        NutritionLogEntity baseEntity = NutritionLogEntity.builder().build();

        when(nutritionLogMapper.toNutritionEntity(nutritionLogDTO)).thenReturn(baseEntity);
        when(nutritionLogRepository.save(any(NutritionLogEntity.class))).thenReturn(nutritionLogEntity);
        when(nutritionLogMapper.toNutritionDto(nutritionLogEntity)).thenReturn(nutritionLogDTO);

        NutritionLogDTO result = nutritionLogService.createLog(nutritionLogDTO, userId);

        assertNotNull(result);
        assertEquals(500, result.getTotalCalories());
        verify(userRepository).findById(userId);
        verify(nutritionLogMapper).toNutritionEntity(nutritionLogDTO);
        verify(nutritionLogRepository).save(any(NutritionLogEntity.class));
        verify(nutritionLogMapper).toNutritionDto(nutritionLogEntity);
    }

    @Test
    void testCreateLogUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> nutritionLogService.createLog(nutritionLogDTO, userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateLog() {
        when(nutritionLogRepository.findById(logId)).thenReturn(Optional.of(nutritionLogEntity));
        NutritionLogDTO updatedDTO = NutritionLogDTO.builder()
                .id(logId)
                .date(LocalDate.now().plusDays(1))
                .totalCalories(600)
                .build();

        NutritionLogEntity updatedEntity = nutritionLogEntity.toBuilder()
                .date(updatedDTO.getDate())
                .totalCalories(updatedDTO.getTotalCalories())
                .build();

        when(nutritionLogRepository.save(any(NutritionLogEntity.class))).thenReturn(updatedEntity);
        when(nutritionLogMapper.toNutritionDto(updatedEntity))
                .thenReturn(updatedDTO);

        NutritionLogDTO result = nutritionLogService.updateLog(logId, updatedDTO);

        assertNotNull(result);
        assertEquals(updatedDTO.getTotalCalories(), result.getTotalCalories());
        assertEquals(updatedDTO.getDate(), result.getDate());
        verify(nutritionLogRepository).findById(logId);
        verify(nutritionLogRepository).save(any(NutritionLogEntity.class));
        verify(nutritionLogMapper).toNutritionDto(updatedEntity);
    }

    @Test
    void testUpdateLogNotFound() {
        when(nutritionLogRepository.findById(logId)).thenReturn(Optional.empty());
        NutritionLogDTO updatedDTO = NutritionLogDTO.builder()
                .id(logId)
                .date(LocalDate.now().plusDays(1))
                .totalCalories(600)
                .build();

        assertThrows(NutritionLogNotFoundException.class, () -> nutritionLogService.updateLog(logId, updatedDTO));
        verify(nutritionLogRepository).findById(logId);
    }

    @Test
    void testDeleteLog() {
        when(nutritionLogRepository.existsById(logId)).thenReturn(true);
        nutritionLogService.deleteLog(logId);
        verify(nutritionLogRepository).existsById(logId);
        verify(nutritionLogRepository).deleteById(logId);
    }

    @Test
    void testDeleteLogNotFound() {
        when(nutritionLogRepository.existsById(logId)).thenReturn(false);
        assertThrows(NutritionLogNotFoundException.class, () -> nutritionLogService.deleteLog(logId));
        verify(nutritionLogRepository).existsById(logId);
    }
}
