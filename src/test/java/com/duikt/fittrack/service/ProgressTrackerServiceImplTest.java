package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.ProgressTrackerDTO;
import com.duikt.fittrack.entity.ProgressTrackerEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.ProgressTrackerNotFoundException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.ProgressTrackerMapper;
import com.duikt.fittrack.repository.ProgressTrackerRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.impl.ProgressTrackerServiceImpl;
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
public class ProgressTrackerServiceImplTest {

    @Mock
    private ProgressTrackerRepository progressTrackerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProgressTrackerMapper progressTrackerMapper;

    private ProgressTrackerServiceImpl progressTrackerService;
    private UUID progressId;
    private UUID userId;
    private ProgressTrackerEntity progressEntity;
    private ProgressTrackerDTO progressDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        progressTrackerService = new ProgressTrackerServiceImpl(progressTrackerRepository, userRepository, progressTrackerMapper);
        progressId = UUID.randomUUID();
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("progressUser")
                .email("progress@example.com")
                .password("password")
                .build();

        progressEntity = ProgressTrackerEntity.builder()
                .id(progressId)
                .date(LocalDate.now())
                .weight(70.0)
                .fatPercentage(20.0)
                .muscleMass(50.0)
                .createdAt(LocalDateTime.now())
                .user(userEntity)
                .build();

        progressDTO = ProgressTrackerDTO.builder()
                .id(progressId)
                .date(LocalDate.now())
                .weight(70.0)
                .fatPercentage(20.0)
                .muscleMass(50.0)
                .build();
    }

    @Test
    void testFindAllProgressEntries() {
        List<ProgressTrackerEntity> entities = List.of(progressEntity);
        List<ProgressTrackerDTO> dtos = List.of(progressDTO);

        when(progressTrackerRepository.findAll()).thenReturn(entities);
        when(progressTrackerMapper.toProgressDtoList(entities)).thenReturn(dtos);

        List<ProgressTrackerDTO> result = progressTrackerService.findAllProgressEntries();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(progressTrackerRepository).findAll();
        verify(progressTrackerMapper).toProgressDtoList(entities);
    }

    @Test
    void testFindProgressEntryById() {
        when(progressTrackerRepository.findById(progressId)).thenReturn(Optional.of(progressEntity));
        when(progressTrackerMapper.toProgressDto(progressEntity)).thenReturn(progressDTO);

        ProgressTrackerDTO result = progressTrackerService.findProgressEntryById(progressId);

        assertNotNull(result);
        assertEquals(progressId, result.getId());
        verify(progressTrackerRepository).findById(progressId);
        verify(progressTrackerMapper).toProgressDto(progressEntity);
    }

    @Test
    void testFindProgressEntryByIdNotFound() {
        when(progressTrackerRepository.findById(progressId)).thenReturn(Optional.empty());

        assertThrows(ProgressTrackerNotFoundException.class, () -> progressTrackerService.findProgressEntryById(progressId));
        verify(progressTrackerRepository).findById(progressId);
    }

    @Test
    void testCreateProgressEntry() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        ProgressTrackerEntity baseEntity = ProgressTrackerEntity.builder()
                .date(progressDTO.getDate())
                .weight(progressDTO.getWeight())
                .fatPercentage(progressDTO.getFatPercentage())
                .muscleMass(progressDTO.getMuscleMass())
                .build();
        when(progressTrackerMapper.toProgressEntity(progressDTO)).thenReturn(baseEntity);

        when(progressTrackerRepository.save(any(ProgressTrackerEntity.class))).thenReturn(progressEntity);
        when(progressTrackerMapper.toProgressDto(progressEntity)).thenReturn(progressDTO);

        ProgressTrackerDTO result = progressTrackerService.createProgressEntry(progressDTO, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(progressTrackerMapper).toProgressEntity(progressDTO);
        verify(progressTrackerRepository).save(any(ProgressTrackerEntity.class));
        verify(progressTrackerMapper).toProgressDto(progressEntity);
    }

    @Test
    void testCreateProgressEntryUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> progressTrackerService.createProgressEntry(progressDTO, userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateProgressEntry() {
        when(progressTrackerRepository.findById(progressId)).thenReturn(Optional.of(progressEntity));

        ProgressTrackerDTO updatedDTO = ProgressTrackerDTO.builder()
                .id(progressId)
                .date(progressDTO.getDate().plusDays(1))
                .weight(72.0)
                .fatPercentage(19.5)
                .muscleMass(51.0)
                .build();

        ProgressTrackerEntity updatedEntity = progressEntity.toBuilder()
                .date(updatedDTO.getDate())
                .weight(updatedDTO.getWeight())
                .fatPercentage(updatedDTO.getFatPercentage())
                .muscleMass(updatedDTO.getMuscleMass())
                .build();

        when(progressTrackerRepository.save(any(ProgressTrackerEntity.class))).thenReturn(updatedEntity);
        when(progressTrackerMapper.toProgressDto(updatedEntity)).thenReturn(updatedDTO);

        ProgressTrackerDTO result = progressTrackerService.updateProgressEntry(progressId, updatedDTO);

        assertNotNull(result);
        assertEquals(72.0, result.getWeight());
        verify(progressTrackerRepository).findById(progressId);
        verify(progressTrackerRepository).save(any(ProgressTrackerEntity.class));
        verify(progressTrackerMapper).toProgressDto(updatedEntity);
    }

    @Test
    void testUpdateProgressEntryNotFound() {
        when(progressTrackerRepository.findById(progressId)).thenReturn(Optional.empty());

        ProgressTrackerDTO updatedDTO = ProgressTrackerDTO.builder()
                .id(progressId)
                .date(LocalDate.now().plusDays(1))
                .weight(72.0)
                .fatPercentage(19.5)
                .muscleMass(51.0)
                .build();

        assertThrows(ProgressTrackerNotFoundException.class, () -> progressTrackerService.updateProgressEntry(progressId, updatedDTO));
        verify(progressTrackerRepository).findById(progressId);
    }

    @Test
    void testDeleteProgressEntry() {
        when(progressTrackerRepository.existsById(progressId)).thenReturn(true);
        progressTrackerService.deleteProgressEntry(progressId);
        verify(progressTrackerRepository).existsById(progressId);
        verify(progressTrackerRepository).deleteById(progressId);
    }

    @Test
    void testDeleteProgressEntryNotFound() {
        when(progressTrackerRepository.existsById(progressId)).thenReturn(false);
        assertThrows(ProgressTrackerNotFoundException.class, () -> progressTrackerService.deleteProgressEntry(progressId));
        verify(progressTrackerRepository).existsById(progressId);
    }
}
