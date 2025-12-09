package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TraineeActivationRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotNull(message = "isActive flag is required")
        Boolean isActive
) {}
