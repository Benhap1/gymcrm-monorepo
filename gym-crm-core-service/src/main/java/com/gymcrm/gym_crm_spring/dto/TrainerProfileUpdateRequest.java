package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerProfileUpdateRequest(
        @NotBlank String username,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String specialization,
        @NotNull Boolean isActive
) {}