package com.gymcrm.gym_crm_spring.dto;

import java.util.UUID;

public record TrainingTypeResponse(
        UUID id,
        String trainingTypeName
) {}
