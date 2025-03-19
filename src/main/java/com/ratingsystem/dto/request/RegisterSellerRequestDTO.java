package com.ratingsystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterSellerRequestDTO(
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
        String password,
        @NotNull(message = "Seller games cannot be null")
        @Valid
        List<RegisterSellerOfferDTO> games
) {
}
