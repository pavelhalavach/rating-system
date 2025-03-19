package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterSellerOfferDTO (
        @NotBlank(message = "Nickname cannot be empty")
        String name,
        @NotBlank(message = "Nickname cannot be empty")
        String description
) {
}