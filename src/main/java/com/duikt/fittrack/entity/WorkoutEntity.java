package com.duikt.fittrack.entity;

import com.duikt.fittrack.domain.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Entity
@Table(name = "workouts")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Workout type is required")
    @Column(nullable = false, length = 50)
    private Type type;

    @PastOrPresent(message = "Workout date must be today or earlier")
    @Column(nullable = false)
    private LocalDate date;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 300, message = "Duration cannot exceed 300 minutes")
    @Column(nullable = false)
    private int duration;

    @Positive(message = "Calories burned must be a positive number")
    @Column(nullable = false)
    private int caloriesBurned;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
