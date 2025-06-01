package com.duikt.fittrack.domain;

import com.duikt.fittrack.domain.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class WorkoutDTO {
    UUID id;

    @NotBlank(message = "Workout type is required")
    @Size(max = 50, message = "Workout type cannot exceed 50 characters")
    @Enumerated(EnumType.STRING)
    Type type;

    @PastOrPresent(message = "Workout date must be today or earlier")
    LocalDate date;

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    @Max(value = 300, message = "Duration cannot exceed 300 minutes")
    int duration;

    @Positive(message = "Calories burned must be a positive number")
    int caloriesBurned;
}
