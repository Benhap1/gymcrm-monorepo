package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.NotBlank;

public record TrainerRegistrationRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Specialization name is required")
        String specializationName
) {}
