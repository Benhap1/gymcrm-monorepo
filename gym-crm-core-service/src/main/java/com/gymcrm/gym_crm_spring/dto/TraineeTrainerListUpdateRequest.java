package com.gymcrm.gym_crm_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TraineeTrainerListUpdateRequest(
        @NotBlank String traineeUsername,
        @NotEmpty List<@NotBlank String> trainersUsernames
) {}
