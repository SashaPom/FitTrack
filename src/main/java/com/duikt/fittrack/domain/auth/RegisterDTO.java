package com.duikt.fittrack.domain.auth;

import com.duikt.fittrack.domain.enums.Gender;
import com.duikt.fittrack.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterDTO {

    @NotNull(message = "Email must be provided")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username must not be empty")
    private String username;

    @NotBlank(message = "Password must not be empty")
    private String password;

    @NotNull(message = "Gender must be provided")
    private Gender gender;

    @NotNull(message = "Age must be provided")
    @Positive(message = "Age must be a positive number")
    private Integer age;

    @NotNull(message = "Weight must be provided")
    @Positive(message = "Weight must be a positive number")
    private Integer weight;

    @NotNull(message = "Height must be provided")
    @Positive(message = "Height must be a positive number")
    private Integer height;

    @NotNull(message = "Role must be provided")
    private Role role;
}
