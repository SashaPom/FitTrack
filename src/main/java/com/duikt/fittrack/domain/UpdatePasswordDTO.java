package com.duikt.fittrack.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdatePasswordDTO {
    @NotBlank(message = "Password must not be blank")
    String password;
    @NotBlank(message = "Password confirmation must not be blank")
    String passwordConf;
}
