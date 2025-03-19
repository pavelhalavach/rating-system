package com.ratingsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailDTO (
        @Email
        @NotBlank(message = "Email cannot be empty or null")
        String email
){
}
