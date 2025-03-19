package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddCommentWithRegRequestDTO(
        @NotBlank(message = "Seller nickname cannot be empty")
        String sellerNickname,
        @NotBlank(message = "Seller first name cannot be empty")
        String sellerFirstName,
        @NotBlank(message = "Seller last name cannot be empty")
        String sellerLastName,
        @NotNull(message = "Seller games cannot be null")
        List<@NotBlank(message = "Game name cannot be empty") String> sellerGames,
        @NotBlank(message = "Comment cannot be empty")
        String commentMessage,
        @NotNull(message = "Rating cannot be null")
        Integer commentRating
) {
}
