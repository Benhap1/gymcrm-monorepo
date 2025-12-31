package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public record TraineeProfileUpdateRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        Optional<LocalDate> dateOfBirth,

        Optional<String> address,

        @NotNull(message = "Active status is required")
        boolean isActive
) {}
