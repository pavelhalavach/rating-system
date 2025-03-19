package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCommentRequestDTO(
        @NotBlank(message = "Comment cannot be empty")
        String commentMessage,
        @NotNull(message = "Rating cannot be null")
        Integer commentRating
) {
}
