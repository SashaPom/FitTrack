package com.duikt.fittrack.domain;

import com.duikt.fittrack.domain.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class UserDTO {
    UUID id;

    @NotNull(message = "Email must be provided")
    @Email(message = "Invalid email format")
    String email;

    @NotNull(message = "Username must be provided")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    String username;

    @NotNull(message = "Gender must be specified")
    @Enumerated(EnumType.STRING)
    Gender gender;

    @Positive(message = "Age must be positive")
    int age;

    @Positive(message = "Weight must be positive")
    int weight;

    @Positive(message = "Height must be positive")
    int height;
}
