package com.duikt.fittrack.domain;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class GoalDTO {
    UUID id;

    @NotBlank(message = "Goal name is required")
    @Size(max = 150, message = "Goal name cannot exceed 150 characters")
    String name;

    @NotBlank(message = "Goal type is required")
    @Size(max = 50, message = "Goal type cannot exceed 50 characters")
    String type;

    @Positive(message = "Target value must be positive")
    int targetValue;

    @Future(message = "Deadline must be in the future")
    LocalDate deadline;
}
