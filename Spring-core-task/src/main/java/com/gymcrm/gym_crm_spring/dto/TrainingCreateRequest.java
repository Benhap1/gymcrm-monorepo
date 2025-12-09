package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TrainingCreateRequest(
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,

        @NotBlank(message = "Trainer username is required")
        String trainerUsername,

        @NotBlank(message = "Training name is required")
        String trainingName,

        @NotNull(message = "Training date is required")
        @FutureOrPresent(message = "Training date cannot be in the past")
        LocalDate trainingDate,

        @NotNull(message = "Training duration is required")
        @Min(value = 10, message = "Training duration must be at least 10 minutes")
        @Max(value = 300, message = "Training duration cannot exceed 300 minutes")
        Integer trainingDuration
) {}
