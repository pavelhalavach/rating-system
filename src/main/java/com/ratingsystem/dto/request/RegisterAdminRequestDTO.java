package com.ratingsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequestDTO (
    @NotBlank(message = "Nickname cannot be empty")
    String nickname,
    @NotBlank(message = "First Name cannot be empty")
    String firstName,
    @NotBlank(message = "Last Name cannot be empty")
    String lastName,
    @NotBlank(message = "Email cannot be empty or null")
    @Email(message = "Invalid email format")
    String email,
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 3, message = "Password must be at least 3 characters")
    String password
) {
}
