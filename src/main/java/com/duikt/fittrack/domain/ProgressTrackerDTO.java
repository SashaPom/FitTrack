package com.duikt.fittrack.domain;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class ProgressTrackerDTO {
    UUID id;

    @PastOrPresent(message = "Date must be today or earlier")
    LocalDate date;

    @Positive(message = "Weight must be positive")
    double weight;

    @DecimalMin(value = "0.0", message = "Fat percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Fat percentage cannot exceed 100%")
    double fatPercentage;

    @Positive(message = "Muscle mass must be positive")
    double muscleMass;
}
