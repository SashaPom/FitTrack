package com.duikt.fittrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.duikt.fittrack.domain.GoalDTO;
import com.duikt.fittrack.domain.enums.Gender;
import com.duikt.fittrack.domain.enums.Role;
import com.duikt.fittrack.entity.GoalEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.exception.GoalNotFoundException;
import com.duikt.fittrack.exception.UserNotFoundException;
import com.duikt.fittrack.mapper.GoalMapper;
import com.duikt.fittrack.repository.GoalRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.service.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalMapper goalMapper;

    private GoalServiceImpl goalService;
    private UUID goalId;
    private UUID userId;
    private GoalEntity goalEntity;
    private GoalDTO goalDTO;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        goalService = new GoalServiceImpl(goalRepository, userRepository, goalMapper);
        goalId = UUID.randomUUID();
        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .age(20)
                .gender(Gender.MALE)
                .weight(90)
                .height(180)
                .role(Role.USER)
                .build();

        goalEntity = GoalEntity.builder()
                .id(goalId)
                .name("Lose Weight")
                .type("Health")
                .targetValue(10)
                .deadline(LocalDate.now().plusDays(30))
                .createdAt(LocalDateTime.now())
                .user(userEntity)
                .build();

        goalDTO = GoalDTO.builder()
                .id(goalId)
                .name("Lose Weight")
                .type("Health")
                .targetValue(10)
                .deadline(LocalDate.now().plusDays(30))
                .build();
    }

    @Test
    void testFindAllGoals() {
        List<GoalEntity> goalsList = List.of(goalEntity);
        List<GoalDTO> goalsDtoList = List.of(goalDTO);

        when(goalRepository.findAll()).thenReturn(goalsList);
        when(goalMapper.toGoalDtoList(goalsList)).thenReturn(goalsDtoList);

        List<GoalDTO> result = goalService.findAllGoals();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lose Weight", result.get(0).getName());
        verify(goalRepository).findAll();
        verify(goalMapper).toGoalDtoList(goalsList);
    }

    @Test
    void testFindGoalById() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalEntity));
        when(goalMapper.toGoalDto(goalEntity)).thenReturn(goalDTO);

        GoalDTO result = goalService.findGoalById(goalId);

        assertNotNull(result);
        assertEquals(goalId, result.getId());
        assertEquals("Lose Weight", result.getName());
        verify(goalRepository).findById(goalId);
        verify(goalMapper).toGoalDto(goalEntity);
    }

    @Test
    void testFindGoalByIdNotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.findGoalById(goalId));
        verify(goalRepository).findById(goalId);
    }

    @Test
    void testCreateGoal() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        GoalEntity baseGoalEntity = GoalEntity.builder()
                .name(goalDTO.getName())
                .type(goalDTO.getType())
                .targetValue(goalDTO.getTargetValue())
                .deadline(goalDTO.getDeadline())
                .build();

        when(goalMapper.toGoalEntity(goalDTO)).thenReturn(baseGoalEntity);
        when(goalRepository.save(any(GoalEntity.class))).thenReturn(goalEntity);
        when(goalMapper.toGoalDto(goalEntity)).thenReturn(goalDTO);

        GoalDTO result = goalService.createGoal(goalDTO, userId);

        assertNotNull(result);
        assertEquals("Lose Weight", result.getName());
        verify(userRepository).findById(userId);
        verify(goalMapper).toGoalEntity(goalDTO);
        verify(goalRepository).save(any(GoalEntity.class));
        verify(goalMapper).toGoalDto(goalEntity);
    }


    @Test
    void testCreateGoalUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> goalService.createGoal(goalDTO, userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testUpdateGoal() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goalEntity));

        GoalDTO updatedDTO = GoalDTO.builder()
                .id(goalId)
                .name("Lose More Weight")
                .type("Health")
                .targetValue(15)
                .deadline(LocalDate.now().plusDays(45))
                .build();

        GoalEntity updatedEntity = goalEntity.toBuilder()
                .name(updatedDTO.getName())
                .targetValue(updatedDTO.getTargetValue())
                .deadline(updatedDTO.getDeadline())
                .build();

        when(goalRepository.save(any(GoalEntity.class))).thenReturn(updatedEntity);
        when(goalMapper.toGoalDto(updatedEntity)).thenReturn(GoalDTO.builder()
                .id(goalId)
                .name("Lose More Weight")
                .type("Health")
                .targetValue(15)
                .deadline(updatedDTO.getDeadline())
                .build());

        GoalDTO result = goalService.updateGoal(goalId, updatedDTO);

        assertNotNull(result);
        assertEquals("Lose More Weight", result.getName());
        assertEquals(15, result.getTargetValue());
        verify(goalRepository).findById(goalId);
        verify(goalRepository).save(any(GoalEntity.class));
        verify(goalMapper).toGoalDto(updatedEntity);
    }

    @Test
    void testUpdateGoalNotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        GoalDTO updatedDTO = GoalDTO.builder()
                .id(goalId)
                .name("Updated Goal")
                .type("Health")
                .targetValue(20)
                .deadline(LocalDate.now().plusDays(60))
                .build();

        assertThrows(GoalNotFoundException.class, () -> goalService.updateGoal(goalId, updatedDTO));
        verify(goalRepository).findById(goalId);
    }

    @Test
    void testDeleteGoal() {
        when(goalRepository.existsById(goalId)).thenReturn(true);

        goalService.deleteGoal(goalId);

        verify(goalRepository).existsById(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void testDeleteGoalNotFound() {
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(GoalNotFoundException.class, () -> goalService.deleteGoal(goalId));
        verify(goalRepository).existsById(goalId);
    }
}
