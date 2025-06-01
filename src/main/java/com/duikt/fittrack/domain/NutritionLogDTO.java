package com.duikt.fittrack.domain;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class NutritionLogDTO {
    UUID id;

    @PastOrPresent(message = "Date must be today or earlier")
    LocalDate date;

    @PositiveOrZero(message = "Total calories cannot be negative")
    int totalCalories;
}
