package com.ratingsystem.dto.response;

public record CommentDTO(
        Integer id,
        String message,
        Integer rating
) {
}
