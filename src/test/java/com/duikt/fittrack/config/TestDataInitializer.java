package com.duikt.fittrack.config;

import com.duikt.fittrack.config.security.jwt.JwtProvider;
import com.duikt.fittrack.domain.enums.Gender;
import com.duikt.fittrack.domain.enums.Role;
import com.duikt.fittrack.domain.enums.Type;
import com.duikt.fittrack.entity.GoalEntity;
import com.duikt.fittrack.entity.NutritionLogEntity;
import com.duikt.fittrack.entity.ProgressTrackerEntity;
import com.duikt.fittrack.entity.UserEntity;
import com.duikt.fittrack.entity.WorkoutEntity;
import com.duikt.fittrack.repository.GoalRepository;
import com.duikt.fittrack.repository.NutritionLogRepository;
import com.duikt.fittrack.repository.ProgressTrackerRepository;
import com.duikt.fittrack.repository.UserRepository;
import com.duikt.fittrack.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataInitializer {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final GoalRepository goalRepository;
    private final NutritionLogRepository nutritionLogRepository;
    private final ProgressTrackerRepository progressTrackerRepository;

    public TestData initTestData() {
        progressTrackerRepository.deleteAll();
        nutritionLogRepository.deleteAll();
        goalRepository.deleteAll();
        workoutRepository.deleteAll();
        userRepository.deleteAll();

        UserEntity adminUser = userRepository.save(
                UserEntity.builder()
                        .email("admin@fittrack.com")
                        .username("FitTrackAdmin")
                        .password("adminpassword")
                        .gender(Gender.MALE)
                        .age(30)
                        .weight(80)
                        .height(180)
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        String adminToken = jwtProvider.createToken(adminUser.getEmail(), adminUser.getRole());

        UserEntity regularUser = userRepository.save(
                UserEntity.builder()
                        .email("user@fittrack.com")
                        .username("FitTrackUser")
                        .password("userpassword")
                        .gender(Gender.FEMALE)
                        .age(25)
                        .weight(60)
                        .height(165)
                        .role(Role.USER)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        String userToken = jwtProvider.createToken(regularUser.getEmail(), regularUser.getRole());


        WorkoutEntity workout = workoutRepository.save(
                WorkoutEntity.builder()
                        .type(Type.RUNNING)
                        .date(LocalDate.now().minusDays(1))
                        .duration(45)
                        .caloriesBurned(400)
                        .user(regularUser)
                        .build()
        );

        GoalEntity goal = goalRepository.save(
                GoalEntity.builder()
                        .name("Improve Stamina")
                        .type("Fitness")
                        .targetValue(100)
                        .deadline(LocalDate.now().plusDays(30))
                        .user(regularUser)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        NutritionLogEntity nutritionLog = nutritionLogRepository.save(
                NutritionLogEntity.builder()
                        .date(LocalDate.now().minusDays(1))
                        .totalCalories(2200)
                        .user(regularUser)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        ProgressTrackerEntity progress = progressTrackerRepository.save(
                ProgressTrackerEntity.builder()
                        .date(LocalDate.now().minusDays(1))
                        .weight(59)
                        .fatPercentage(18.5)
                        .muscleMass(45)
                        .user(regularUser)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return new TestData(
                adminUser.getId(),
                regularUser.getId(),
                workout.getId(),
                goal.getId(),
                nutritionLog.getId(),
                progress.getId(),
                adminToken,
                userToken
        );
    }

    public record TestData(
            UUID adminUserId,
            UUID regularUserId,
            UUID workoutId,
            UUID goalId,
            UUID nutritionLogId,
            UUID progressTrackerId,
            String adminToken,
            String userToken
    ) {}
}
