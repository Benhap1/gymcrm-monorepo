package com.gymcrm.gym_crm_spring.dto;

public record TrainerShortResponse(
        String username,
        String firstName,
        String lastName,
        String specialization
) {}
