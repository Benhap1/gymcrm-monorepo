package com.gymcrm.gym_crm_spring.dto;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Optional;

public record TraineeRegistrationRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        Optional<LocalDate> dateOfBirth,

        Optional<String> address
    ) {}
