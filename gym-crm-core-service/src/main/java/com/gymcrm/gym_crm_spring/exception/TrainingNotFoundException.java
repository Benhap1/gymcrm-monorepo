package com.gymcrm.gym_crm_spring.exception;

import java.util.UUID;

public class TrainingNotFoundException extends RuntimeException {

    public TrainingNotFoundException(UUID trainingId) {
        super("Training not found: " + trainingId);
    }
}
