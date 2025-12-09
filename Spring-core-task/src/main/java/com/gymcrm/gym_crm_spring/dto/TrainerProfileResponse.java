package com.gymcrm.gym_crm_spring.dto;

import java.util.List;

public record TrainerProfileResponse(
        String firstName,
        String lastName,
        String specialization,
        boolean isActive,
        List<TraineeShortResponse> trainees
) {}
