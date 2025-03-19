package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSellerRequestDTO(
        @NotBlank@NotBlank(message = "Description cannot be empty or null")
        String updatedDescription
) {
}
