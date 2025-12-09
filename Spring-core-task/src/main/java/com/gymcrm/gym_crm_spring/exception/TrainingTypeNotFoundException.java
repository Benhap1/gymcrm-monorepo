package com.gymcrm.gym_crm_spring.exception;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String trainingTypeName) {
        super("Training type not found: " + trainingTypeName);
    }
}
