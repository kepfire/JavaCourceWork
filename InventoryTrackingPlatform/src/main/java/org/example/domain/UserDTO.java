package org.example.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder(toBuilder = true)
public class UserDTO {
    UUID userId;

    @NotBlank(message = "Username must not be empty")
    String username;

    @NotNull(message = "Email must be provided")
    @Email(message = "Invalid email format")
    String email;
}