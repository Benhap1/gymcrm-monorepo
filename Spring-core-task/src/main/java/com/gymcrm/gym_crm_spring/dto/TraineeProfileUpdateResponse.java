package com.gymcrm.gym_crm_spring.dto;

import java.time.LocalDate;
import java.util.List;

public record TraineeProfileUpdateResponse(
         String username,
         String firstName,
         String lastName,
         LocalDate dateOfBirth,
         String address,
         boolean isActive,
         List<TrainerShortResponse> trainers
) {}
