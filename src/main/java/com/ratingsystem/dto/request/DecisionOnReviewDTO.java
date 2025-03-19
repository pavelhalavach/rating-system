package com.ratingsystem.dto.request;

import jakarta.validation.constraints.NotNull;

public record DecisionOnReviewDTO(
        @NotNull(message = "Decision cannot be null")
        Boolean decision
){
}
