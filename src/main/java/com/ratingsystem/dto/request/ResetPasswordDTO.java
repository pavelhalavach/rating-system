package com.ratingsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @Email
        @NotBlank(message = "Email cannot be empty or null")
        String email,
        @NotBlank(message = "Password cannot be empty or null")
        @Size(min = 3, message = "Password must be at least 3 characters")
        String newPassword
) {
}
