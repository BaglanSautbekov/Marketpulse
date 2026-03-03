package com.marketpulse.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 200) String password,
    @NotBlank @Size(min = 2, max = 80) String workspaceName
) {}
