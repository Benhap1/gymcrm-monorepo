package com.gymcrm.gym_crm_spring.dto;

import java.util.List;

public record TraineeTrainerListUpdateResponse(
        List<TrainerShortResponse> trainers
) {}
