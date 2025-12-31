package com.gymcrm.gym_crm_spring.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record TraineeTrainingsFilterRequest(
        String username,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
        String trainerName,
        String trainingType
) {}