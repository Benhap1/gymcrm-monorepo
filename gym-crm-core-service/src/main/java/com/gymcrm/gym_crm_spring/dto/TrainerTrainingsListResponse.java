package com.gymcrm.gym_crm_spring.dto;

import java.time.LocalDate;
import java.util.List;

public record TrainerTrainingsListResponse(
        List<TrainerTrainingResponse> trainings
) {
    public record TrainerTrainingResponse(
            String trainingName,
            LocalDate trainingDate,
            String trainingType,
            int trainingDuration,
            String traineeUsername
    ) {}
}